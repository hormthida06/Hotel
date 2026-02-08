
    // Navbar scroll effect
    window.addEventListener('scroll', function() {
    const navbar = document.querySelector('.navbar');
    if (window.scrollY > 50) {
    navbar.classList.add('scrolled');
} else {
    navbar.classList.remove('scrolled');
}
});

    // Smooth scrolling for navigation links
    document.querySelectorAll('a[href^="#"]').forEach(anchor => {
    anchor.addEventListener('click', function (e) {
        e.preventDefault();
        const target = document.querySelector(this.getAttribute('href'));
        if (target) {
            target.scrollIntoView({
                behavior: 'smooth',
                block: 'start'
            });
        }
    });
});

    // Date picker logic
    const checkinInput = document.getElementById('checkin');
    const checkoutInput = document.getElementById('checkout');

    checkinInput.addEventListener('change', function() {
    const checkinDate = new Date(this.value);
    checkoutInput.min = this.value;
    checkoutInput.value = new Date(checkinDate.getTime() + 24*60*60*1000).toISOString().split('T')[0];
});

    // Set minimum date to today
    const today = new Date().toISOString().split('T')[0];
    checkinInput.min = today;
    checkoutInput.min = today;

    // Search rooms function
    function searchRooms() {
    const checkin = checkinInput.value;
    const checkout = checkoutInput.value;
    const guests = document.getElementById('guests').value;

    if (!checkin || !checkout) {
    alert('Please select check-in and check-out dates');
    return;
}

    // Simulate search
    alert(`Searching for rooms from ${checkin} to ${checkout} for ${guests}...\n\nResults will appear here!`);

    // Scroll to rooms
    document.querySelector('#rooms').scrollIntoView({ behavior: 'smooth' });
}

    // Room booking buttons
    document.querySelectorAll('.room-card .btn').forEach(btn => {
    btn.addEventListener('click', function() {
        alert('Booking functionality would integrate with your reservation system here!');
    });
});

    // Initialize date to today +1 and tomorrow
    checkinInput.value = today;
    checkoutInput.value = new Date(Date.now() + 24*60*60*1000).toISOString().split('T')[0];

    // Counter animation for stats (if you add a stats section)
    function animateCounters() {
    // Add your counter animation logic here
}
