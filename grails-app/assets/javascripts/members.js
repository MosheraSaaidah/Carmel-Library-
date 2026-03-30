<<<<<<< HEAD
document.addEventListener('DOMContentLoaded', function () {
    var modalEl = document.getElementById('editMemberModal');
    if (!modalEl) {
        return;
    }
    modalEl.addEventListener('show.bs.modal', function (e) {
        var t = e.relatedTarget;
        if (!t || !t.closest) {
            return;
        }
        var btn = t.closest('.btn-edit-member');
        if (!btn) {
            return;
        }
        openEditMember(
            btn.getAttribute('data-member-id'),
            btn.getAttribute('data-full-name') || '',
            btn.getAttribute('data-email') || '',
            btn.getAttribute('data-phone') || '',
            btn.getAttribute('data-address') || ''
        );
    });
});

function openEditMember(id, fullName, email, phoneNumber, address) {
    var root = document.getElementById('editMemberModal');
    if (!root) {
        return;
    }
    var idEl = root.querySelector('#editMemberId');
    var nameEl = root.querySelector('#editFullName');
    var emailEl = root.querySelector('#editEmail');
    var phoneEl = root.querySelector('#editPhone');
    var addrEl = root.querySelector('#editAddress');
    if (!idEl || !nameEl || !emailEl || !phoneEl || !addrEl) {
        return;
    }

    idEl.value = id != null && id !== 'null' ? String(id) : '';
    nameEl.value = fullName;
    emailEl.value = email;
    phoneEl.value = phoneNumber;
    addrEl.value = address || '';
}
=======
function openEditMember(id, fullName, email, phoneNumber, address) {
    document.getElementById('editMemberId').value  = id;
    document.getElementById('editFullName').value  = fullName;
    document.getElementById('editEmail').value     = email;
    document.getElementById('editPhone').value     = phoneNumber;
    document.getElementById('editAddress').value   = address;

    new bootstrap.Modal(document.getElementById('editMemberModal')).show();
}
>>>>>>> e66bcc56455ff1dabd506a74f52d86e3e725c50e
