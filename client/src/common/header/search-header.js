function searchResults() {
	var str = document.getElementById('search').value;
	str = str.replace(/[^a-zA-Z0-9\s&-]/g, '');
	document.location = ('/search?q='+encodeURIComponent(str));
}
	
$(document).ready(function() {
	$("#search").keypress(function (key) {
		if (key.charCode !== 0) {
			if (String.fromCharCode(key.charCode).match(/[^a-zA-Z0-9\s\n&-]/g)) {
				return false;
			}
		}
	});
});