document.addEventListener('DOMContentLoaded', function () {
    const success = document.body.dataset.success;
    const error   = document.body.dataset.error;

    // Global success toast, used after create/update/delete actions.
    if (success && success.trim() !== '') {
        Swal.fire({
            toast            : true,
            position         : 'bottom-end',
            icon             : 'success',
            title            : success,
            showConfirmButton: false,
            timer            : 3000,
            timerProgressBar : true
        });
    }

    // Global error toast, used for validation and business‑rule errors.
    if (error && error.trim() !== '') {
        Swal.fire({
            toast            : true,
            position         : 'bottom-end',
            icon             : 'error',
            title            : error,
            showConfirmButton: false,
            timer            : 3500,
            timerProgressBar : true
        });
    }
});