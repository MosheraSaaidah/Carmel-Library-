// Chart.js للـ Dashboard — رسم "Borrowing Activity (Last 7 Days)"
document.addEventListener('DOMContentLoaded', function () {
    const canvas = document.getElementById('borrowingChart');
    if (!canvas || typeof Chart === 'undefined') return;

    var labels = [];
    var values = [];
    try {
        if (canvas.dataset.labels) labels = JSON.parse(canvas.dataset.labels);
        if (canvas.dataset.values) values = JSON.parse(canvas.dataset.values);
    } catch (e) {
        console.warn('Dashboard chart: invalid data', e);
    }
    if (!Array.isArray(labels)) labels = [];
    if (!Array.isArray(values)) values = [];

    new Chart(canvas.getContext('2d'), {
        type: 'line',
        data: {
            labels: labels,
            datasets: [{
                label: (canvas.dataset.chartLabel || 'Books Borrowed'),
                data: values,
                borderColor: '#16a34a',
                backgroundColor: 'rgba(22,163,74,0.08)',
                borderWidth: 2,
                pointBackgroundColor: '#16a34a',
                tension: 0.4,
                fill: true
            }]
        },
        options: {
            responsive: true,
            maintainAspectRatio: true,
            plugins: { legend: { display: false } },
            scales: {
                y: {
                    beginAtZero: true,
                    ticks: { stepSize: 1 },
                    grid: { color: '#f0f0f0' }
                },
                x: { grid: { display: false } }
            }
        }
    });
});