
    // Revenue Chart (Line)
    const ctxRevenue = document.getElementById('revenueChart').getContext('2d');
    new Chart(ctxRevenue, {
    type: 'line',
    data: {
    labels: ['Jan 1', 'Jan 3', 'Jan 5', 'Jan 7', 'Jan 9', 'Jan 11', 'Jan 13', 'Jan 15'],
    datasets: [{
    label: 'Revenue ($)',
    data: [12400, 15800, 19200, 23100, 27800, 30500, 34200, 38920],
    borderColor: '#ed8936',
    backgroundColor: 'rgba(237,137,54,0.15)',
    tension: 0.4,
    fill: true
}, {
    label: 'Bookings',
    data: [42, 58, 71, 89, 112, 134, 158, 187],
    borderColor: '#2c5282',
    tension: 0.4
}]
},
    options: {
    responsive: true,
    plugins: { legend: { position: 'top' } },
    scales: { y: { beginAtZero: true } }
}
});

    // Room Type Pie Chart
    const ctxRoomType = document.getElementById('roomTypeChart').getContext('2d');
    new Chart(ctxRoomType, {
    type: 'doughnut',
    data: {
    labels: ['Deluxe', 'Suite', 'Double', 'Single'],
    datasets: [{
    data: [42, 28, 19, 11],
    backgroundColor: ['#2c5282', '#ed8936', '#38bdf8', '#10b981']
}]
},
    options: {
    responsive: true,
    plugins: {
    legend: { position: 'bottom' },
    tooltip: { callbacks: { label: (ctx) => `${ctx.label}: ${ctx.raw}%` } }
},
    cutout: '65%'
}
});
