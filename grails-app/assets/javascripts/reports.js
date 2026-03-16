document.addEventListener('DOMContentLoaded', function () {
    if (typeof Chart === 'undefined') return;

    const booksStatusCanvas = document.getElementById('booksStatusChart');
    if (booksStatusCanvas) {
        const active = parseInt(booksStatusCanvas.dataset.active || '0', 10);
        const archived = parseInt(booksStatusCanvas.dataset.archived || '0', 10);

        new Chart(booksStatusCanvas.getContext('2d'), {
            type: 'pie',
            data: {
                labels: ['Active Titles', 'Archived Titles'],
                datasets: [{
                    data: [active, archived],
                    backgroundColor: ['#16a34a', '#9ca3af']
                }]
            },
            options: {
                plugins: {
                    legend: {
                        position: 'bottom'
                    }
                }
            }
        });
    }

    const memberActivityCanvas = document.getElementById('memberActivityChart');
    if (memberActivityCanvas) {
        let labels = [];
        let values = [];
        try {
            labels = JSON.parse(memberActivityCanvas.dataset.labels || '[]');
            values = JSON.parse(memberActivityCanvas.dataset.values || '[]');
        } catch (e) {
            console.warn('Members activity chart: invalid data', e);
        }

        new Chart(memberActivityCanvas.getContext('2d'), {
            type: 'bar',
            data: {
                labels: labels,
                datasets: [{
                    label: 'Borrowings',
                    data: values,
                    backgroundColor: '#16a34a'
                }]
            },
            options: {
                indexAxis: 'y',
                scales: {
                    x: { beginAtZero: true }
                },
                plugins: {
                    legend: { display: false }
                }
            }
        });
    }

    const popularBooksCanvas = document.getElementById('popularBooksChart');
    if (popularBooksCanvas) {
        let labels = [];
        let values = [];
        try {
            labels = JSON.parse(popularBooksCanvas.dataset.labels || '[]');
            values = JSON.parse(popularBooksCanvas.dataset.values || '[]');
        } catch (e) {
            console.warn('Popular books chart: invalid data', e);
        }

        new Chart(popularBooksCanvas.getContext('2d'), {
            type: 'bar',
            data: {
                labels: labels,
                datasets: [{
                    label: 'Borrowings',
                    data: values,
                    backgroundColor: 'rgba(22,163,74,0.9)'
                }]
            },
            options: {
                scales: {
                    y: { beginAtZero: true }
                },
                plugins: { legend: { display: false } }
            }
        });
    }

    const borrowingOverTimeCanvas = document.getElementById('borrowingOverTimeChart');
    if (borrowingOverTimeCanvas) {
        let monthLabels = [];
        let monthValues = [];
        let yearLabels = [];
        let yearValues = [];
        try {
            monthLabels = JSON.parse(borrowingOverTimeCanvas.dataset.monthLabels || '[]');
            monthValues = JSON.parse(borrowingOverTimeCanvas.dataset.monthValues || '[]');
            yearLabels = JSON.parse(borrowingOverTimeCanvas.dataset.yearLabels || '[]');
            yearValues = JSON.parse(borrowingOverTimeCanvas.dataset.yearValues || '[]');
        } catch (e) {
            console.warn('Borrowing over time chart: invalid data', e);
        }

        new Chart(borrowingOverTimeCanvas.getContext('2d'), {
            type: 'line',
            data: {
                labels: monthLabels.length ? monthLabels : yearLabels,
                datasets: [{
                    label: 'Monthly Borrowings',
                    data: monthValues,
                    borderColor: '#16a34a',
                    backgroundColor: 'rgba(22,163,74,0.1)',
                    tension: 0.3
                }, {
                    label: 'Yearly Borrowings',
                    data: yearValues,
                    borderColor: '#3b82f6',
                    backgroundColor: 'rgba(59,130,246,0.1)',
                    tension: 0.3
                }]
            },
            options: {
                responsive: true,
                plugins: {
                    legend: { position: 'bottom' }
                },
                scales: {
                    y: { beginAtZero: true }
                }
            }
        });
    }
});

