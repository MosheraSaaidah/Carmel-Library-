function openEditMember(id, fullName, email, phoneNumber, address) {
    document.getElementById('editMemberId').value  = id;
    document.getElementById('editFullName').value  = fullName;
    document.getElementById('editEmail').value     = email;
    document.getElementById('editPhone').value     = phoneNumber;
    document.getElementById('editAddress').value   = address;

    new bootstrap.Modal(document.getElementById('editMemberModal')).show();
}