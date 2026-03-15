
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

    var searchTimer;

    searchInput.addEventListener('input', function () {
        clearTimeout(searchTimer);
        searchTimer = setTimeout(function () {
            const query = searchInput.value.trim();
            const url  = '?' + (query ? 'q=' + encodeURIComponent(query) : '');

            fetch(('' + window.location.pathname) + url, {
                method: 'GET',
                headers: { 'X-Requested-With': 'XMLHttpRequest' }
            })
                .then(function (r) { return r.text(); })
                .then(function (html) {
                    booksGrid.innerHTML = html;
                    // تحديث الرابط في المتصفح بدون إعادة تحميل (مثلاً للمشاركة أو التحديث لاحقاً)
                    window.history.replaceState({}, '', window.location.pathname + url);
                })
                .catch(function () {
                    // لو فشل الـ AJAX نرجع للطريقة القديمة (تحميل كامل الصفحة)
                    window.location.href = window.location.pathname + url;
                });
        }, 350);
    });
});




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