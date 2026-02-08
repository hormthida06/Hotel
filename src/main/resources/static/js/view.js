
    document.addEventListener("DOMContentLoaded", function() {
    const readMoreLinks = document.querySelectorAll(".read-more");

    readMoreLinks.forEach(link => {
    link.addEventListener("click", function(e) {
    e.preventDefault();

    const td = this.parentElement;
    const shortText = td.querySelector(".short-text");
    const fullText = td.querySelector(".full-text");

    if (fullText.classList.contains("d-none")) {
    // Show full text
    fullText.classList.remove("d-none");
    shortText.classList.add("d-none");
    this.textContent = "Read Less";
} else {
    // Show short text
    fullText.classList.add("d-none");
    shortText.classList.remove("d-none");
    this.textContent = "Read More";
}
});
});
});

