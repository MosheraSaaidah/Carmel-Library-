/**
 * Archive forms: SweetAlert2 confirm (capture phase — works for AJAX-replaced DOM too).
 * Native form.submit() skips a second submit event, so no loop.
 */
(function () {
    'use strict';

    document.addEventListener('submit', function (e) {
        var form = e.target;
        if (!form || form.tagName !== 'FORM' || !form.classList.contains('archive-confirm-form')) {
            return;
        }
        e.preventDefault();
        e.stopPropagation();

        var title = form.getAttribute('data-archive-title') || 'Archive?';
        var text = form.getAttribute('data-archive-text') || '';
        var confirmLabel = form.getAttribute('data-confirm-label') || 'Yes, archive';

        function submitNative() {
            HTMLFormElement.prototype.submit.call(form);
        }

        if (typeof Swal === 'undefined') {
            if (window.confirm((text && text.length) ? (title + '\n\n' + text) : title)) {
                submitNative();
            }
            return;
        }

        Swal.fire({
            icon               : 'warning',
            title              : title,
            text               : text,
            showCancelButton   : true,
            confirmButtonText  : confirmLabel,
            cancelButtonText   : 'Cancel',
            confirmButtonColor : '#dc2626',
            cancelButtonColor  : '#6b7280',
            reverseButtons     : true
        }).then(function (result) {
            if (result.isConfirmed) {
                submitNative();
            }
        });
    }, true);
})();

/**
 * Category edit modal: fill fields and open via JS (only when #editCategoryModal is on the page).
 * Loaded with every page so it does not depend on a separate per-controller asset.
 */
(function () {
    'use strict';

    function bindCategoryEditModal() {
        if (window.__alCarmelCategoryEditBound) {
            return;
        }
        var modalEl = document.getElementById('editCategoryModal');
        if (!modalEl) {
            return;
        }
        window.__alCarmelCategoryEditBound = true;

        modalEl.addEventListener('show.bs.modal', function (e) {
            var t = e.relatedTarget;
            if (!t || !t.closest) {
                return;
            }
            var btn = t.closest('.btn-edit-category');
            if (!btn) {
                return;
            }
            var idEl = modalEl.querySelector('#editCategoryRecordId');
            var nameEl = modalEl.querySelector('#editCategoryNameInput');
            if (!idEl || !nameEl) {
                return;
            }
            var cid = btn.getAttribute('data-category-id');
            idEl.value = cid != null && cid !== 'null' ? String(cid) : '';
            nameEl.value = btn.getAttribute('data-category-name') || '';
        });
    }

    if (document.readyState === 'loading') {
        document.addEventListener('DOMContentLoaded', bindCategoryEditModal);
    } else {
        bindCategoryEditModal();
    }
})();
