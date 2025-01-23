document.getElementById('pageNameSelect').addEventListener('change', function() {
    const form = document.getElementById('pageForm');
    if (this.value) {
        form.submit();
    }
});