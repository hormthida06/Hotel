

    document.addEventListener('DOMContentLoaded', () => {

    flatpickr(".date-range", {
        mode: "range",
        dateFormat: "Y-m-d", // good for backend
        minDate: "today",
        weekStart: 1,
        maxDate: new Date(new Date().setDate(new Date().getDate() + 30)), // max 30 days
        defaultDate: [
            new Date(),
            new Date(new Date().setDate(new Date().getDate() + 1)) // tomorrow
        ],

        onClose: function (selectedDates) {
            const checkinInput = document.getElementById('checkin');
            const checkoutInput = document.getElementById('checkout');

            const today = new Date();
            const tomorrow = new Date();
            tomorrow.setDate(today.getDate() + 1);

            if (selectedDates.length === 2) {
                checkinInput.value = selectedDates[0].toISOString().split('T')[0];
                checkoutInput.value = selectedDates[1].toISOString().split('T')[0];
            } else {
                // ✅ default fallback
                checkinInput.value = today.toISOString().split('T')[0];
                checkoutInput.value = tomorrow.toISOString().split('T')[0];
            }
        }
    });

    const adultsInput    = document.getElementById('adultsCount');
    const childrenInput  = document.getElementById('childrenCount');
    const roomsInput     = document.getElementById('roomsCount');
    const summaryEl      = document.getElementById('guestsSummary');
    const doneBtn        = document.querySelector('.done-btn');

    function updateSummary() {
    const a = parseInt(adultsInput.value)   || 1;
    const c = parseInt(childrenInput.value) || 0;
    const r = parseInt(roomsInput.value)    || 1;

    summaryEl.textContent = `${a} adult${a !== 1 ? 's' : ''} · ${c} ${c === 1 ? 'child' : 'children'} · ${r} room${r !== 1 ? 's' : ''}`;
}

    document.querySelectorAll('.increment, .decrement').forEach(btn => {
    btn.addEventListener('click', () => {
    const type = btn.dataset.type;
    const input = document.getElementById(type + 'Count');
    let val = parseInt(input.value) || 0;

    if (btn.classList.contains('increment')) {
    if (type === 'adults'   && val < 10) val++;
    if (type === 'children' && val < 10) val++;
    if (type === 'rooms'    && val < 5)  val++;
} else {
    if (type === 'adults'   && val > 1)  val--;
    if (type === 'children' && val > 0)  val--;
    if (type === 'rooms'    && val > 1)  val--;
}

    input.value = val;
    updateSummary();
});
});

    doneBtn.addEventListener('click', () => {
    const dropdown = document.querySelector('.guests-dropdown.show');
    if (dropdown) dropdown.classList.remove('show');
    summaryEl.setAttribute('aria-expanded', 'false');
});

    // Initial
    updateSummary();
});
