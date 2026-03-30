<<<<<<< HEAD
/**
 * Remember which book to load: capture phase runs before Bootstrap handles the click, so we never
 * depend on show.bs.modal's relatedTarget (often null in BS5 / icon clicks / AJAX-replaced rows).
 */
document.addEventListener('click', function (ev) {
    var el = ev.target;
    if (!el || typeof el.closest !== 'function') {
        return;
    }
    var b = el.closest('.btn-edit-book');
    if (!b) {
        return;
    }
    var modal = document.getElementById('editBookModal');
    if (!modal) {
        return;
    }
    var bid = b.getAttribute('data-id');
    modal.dataset.pendingBookId = (bid != null && bid !== '') ? String(bid) : '';
}, true);

document.addEventListener('DOMContentLoaded', function () {
    var searchInput = document.querySelector('.search-input');
    var booksGrid = document.getElementById('booksGrid');

    var addModal = document.getElementById('addBookModal');
    if (addModal) {
        addModal.addEventListener('show.bs.modal', function () {
            var sel = addModal.querySelector('select[name="categoryId"]');
            if (sel) {
                sel.value = '';
            }
        });
    }

    var editModal = document.getElementById('editBookModal');
    if (editModal) {
        editModal.addEventListener('shown.bs.modal', function () {
            var id = editModal.dataset.pendingBookId;
            if (id == null || id === '') {
                return;
            }
            delete editModal.dataset.pendingBookId;
            loadBookForEdit(id);
        });
    }

    if (!searchInput || !booksGrid) {
        return;
    }
=======

document.addEventListener('DOMContentLoaded', function () {
    const searchInput = document.querySelector('.search-input');
    const booksGrid  = document.getElementById('booksGrid');

    if (booksGrid) {
        booksGrid.addEventListener('click', function (e) {
            var btn = e.target && e.target.closest('.btn-edit-book');
            if (!btn) return;
            e.preventDefault();
            openEditBook(
                btn.dataset.id,
                btn.dataset.title || '',
                btn.dataset.author || '',
                btn.dataset.description || '',
                parseInt(btn.dataset.totalCopies, 10) || 0,
                parseInt(btn.dataset.publishYear, 10) || 0,
                parseInt(btn.dataset.categoryId, 10) || 0
            );
        });
    }

    if (!searchInput || !booksGrid) return;
>>>>>>> e66bcc56455ff1dabd506a74f52d86e3e725c50e

    var searchTimer;

    searchInput.addEventListener('input', function () {
        clearTimeout(searchTimer);
        searchTimer = setTimeout(function () {
<<<<<<< HEAD
            var query = searchInput.value.trim();
            var url = '?' + (query ? 'q=' + encodeURIComponent(query) : '');

            fetch(('' + window.location.pathname) + url, {
                method : 'GET',
=======
            const query = searchInput.value.trim();
            const url  = '?' + (query ? 'q=' + encodeURIComponent(query) : '');

            fetch(('' + window.location.pathname) + url, {
                method: 'GET',
>>>>>>> e66bcc56455ff1dabd506a74f52d86e3e725c50e
                headers: { 'X-Requested-With': 'XMLHttpRequest' }
            })
                .then(function (r) { return r.text(); })
                .then(function (html) {
                    booksGrid.innerHTML = html;
<<<<<<< HEAD
                    window.history.replaceState({}, '', window.location.pathname + url);
                })
                .catch(function () {
=======
                    // تحديث الرابط في المتصفح بدون إعادة تحميل (مثلاً للمشاركة أو التحديث لاحقاً)
                    window.history.replaceState({}, '', window.location.pathname + url);
                })
                .catch(function () {
                    // لو فشل الـ AJAX نرجع للطريقة القديمة (تحميل كامل الصفحة)
>>>>>>> e66bcc56455ff1dabd506a74f52d86e3e725c50e
                    window.location.href = window.location.pathname + url;
                });
        }, 350);
    });
});

<<<<<<< HEAD
function getBookEditUrl(apiCfg, id) {
    if (!apiCfg || id == null || id === '') {
        return null;
    }
    var base = (apiCfg.getAttribute('data-book-edit-base') || '').replace(/\/+$/, '');
    if (!base) {
        return null;
    }
    return base + '/' + encodeURIComponent(id);
}

function loadBookForEdit(id) {
    var apiCfg = document.getElementById('book-page-api-config');
    var url = getBookEditUrl(apiCfg, id);
    var root = document.getElementById('editBookModal');
    if (!url || !root) {
        return;
    }

    var editId = root.querySelector('#editBookId');
    var editTitle = root.querySelector('#editTitle');
    var editAuthor = root.querySelector('#editAuthor');
    var editDesc = root.querySelector('#editDescription');
    var editCopies = root.querySelector('#editTotalCopies');
    var editYear = root.querySelector('#editPublishYear');
    var editCat = root.querySelector('#editCategoryId');

    fetch(url, {
        method     : 'GET',
        credentials: 'same-origin',
        cache      : 'no-store',
        headers    : { 'X-Requested-With': 'XMLHttpRequest', 'Accept': 'application/json' }
    })
        .then(function (r) {
            if (!r.ok) {
                throw new Error('load failed');
            }
            return r.json();
        })
        .then(function (data) {
            if (!data || data.error) {
                throw new Error('not found');
            }
            if (editId) {
                editId.value = data.id != null ? String(data.id) : '';
            }
            if (editTitle) {
                editTitle.value = data.bookTitle || '';
            }
            if (editAuthor) {
                editAuthor.value = data.authorName || '';
            }
            if (editDesc) {
                editDesc.value = data.description || '';
            }
            if (editCopies) {
                var tc = data.totalCopies;
                editCopies.value = tc != null && tc !== '' && Number(tc) > 0 ? String(tc) : '';
            }
            if (editYear) {
                var py = data.publishYear;
                editYear.value = py != null && py !== '' && !isNaN(Number(py)) ? String(py) : '';
            }
            if (editCat) {
                var cid = data.categoryId;
                var catVal = cid != null && cid !== '' && !isNaN(Number(cid)) && Number(cid) > 0
                    ? String(cid)
                    : '';
                editCat.value = catVal;
            }
        })
        .catch(function () {
            if (typeof Swal !== 'undefined') {
                Swal.fire({ icon: 'error', title: 'Could not load book', text: 'Please refresh and try again.' });
            }
            try {
                var inst = bootstrap.Modal.getInstance(root);
                if (inst) {
                    inst.hide();
                }
            } catch (e) { /* no-op */ }
        });
}
=======



function openEditBook(id, bookTitle, authorName, description, totalCopies, publishYear, categoryId) {
    document.getElementById('editBookId').value      = id;
    document.getElementById('editTitle').value       = bookTitle;
    document.getElementById('editAuthor').value      = authorName;
    document.getElementById('editDescription').value = description || '';
    document.getElementById('editTotalCopies').value = totalCopies;
    document.getElementById('editPublishYear').value = publishYear;
    document.getElementById('editCategoryId').value  = categoryId;

    new bootstrap.Modal(document.getElementById('editBookModal')).show();
}
>>>>>>> e66bcc56455ff1dabd506a74f52d86e3e725c50e
