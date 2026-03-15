package alCarmel

import grails.gorm.transactions.Transactional

@Transactional
class MemberService {

    /** List only active (non-archived) members for normal UI. */
    def getMembers() {
        Member.findAllByActive(true, [sort: 'fullName', order: 'asc'])
    }

    /** List only archived members for admin/archived view. */
    def getArchivedMembers() {
        Member.findAllByActive(false, [sort: 'archivedAt', order: 'desc'])
    }

    def saveMember(Map params) {
        def phone = params.phoneNumber?.toString()?.trim() ?: ''
        def member = new Member(
                fullName      : params.fullName,
                email         : params.email,
                phoneNumber   : phone,
                address       : params.address,
                membershipDate: new Date(),
                active        : true
        )
        member.save()
        member
    }

    def updateMember(Long id, Map params) {
        def member         = Member.get(id)
        if (!member) return null
        member.fullName    = params.fullName
        member.email       = params.email
        member.phoneNumber = params.phoneNumber?.toString()?.trim() ?: params.phoneNumber
        member.address     = params.address
        member.save()
        member
    }

   
    def archiveMember(Long id, Long archivedByMemberId = null) {
        def member = Member.get(id)
        if (!member) return null
        if (!member.active) return member // already archived

        def activeCount = Borrow.withCriteria {
            eq('member', member)
            'in'('status', ['BORROWED', 'LATE'])
            projections { rowCount() }
        }[0] ?: 0
        if (activeCount > 0) {
            throw new IllegalStateException("Cannot archive member: they have ${activeCount} active borrow(s). Please ensure all books are returned first.")
        }

        member.active = false
        member.archivedAt = new Date()
        member.archivedBy = archivedByMemberId ? Member.get(archivedByMemberId) : null
        member.save()
        member
    }

    /** Restore an archived member. */
    def restoreMember(Long id) {
        def member = Member.get(id)
        if (!member) return null
        member.active = true
        member.archivedAt = null
        member.archivedBy = null
        member.save()
        member
    }

    /** Count active members (for reports). */
    def countActive() {
        Member.countByActive(true)
    }

    /** Count archived members (for reports). */
    def countArchived() {
        Member.countByActive(false)
    }
}
