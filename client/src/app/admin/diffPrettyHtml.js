var lineDiff = (function() {
	return {
		diffTwoTexts : function(text1, text2) {
			var dmp = new diff_match_patch();
			console.log('text1 : ' + text1 + ' ' + 'text2 : ' + text2);
			var a = dmp.diff_linesToChars_(text2, text1);
			var lineText1 = a.chars1;
			var lineText2 = a.chars2;
			var lineArray = a.lineArray;

			var diffs = dmp.diff_main(lineText1, lineText2, false);

			dmp.diff_charsToLines_(diffs, lineArray);
			var prettyHtml = createPrettyHTML.withDiff(diffs);
			//dmp.diff_cleanupSemantic(diffs);
			console.log('prettyHTML : ' + prettyHtml);
			return prettyHtml;
		}	
	};
	
})();

var createPrettyHTML = (function() {
	var pattern_amp = /&/g;
	var pattern_lt = /</g;
	var pattern_gt = />/g;
	var pattern_para = /\n/g;
	var div_ins_start = '<div style="color:green">';
	var div_delete_start = '<div style="color:red">';
	var div_end = '</div>';
	
	return {
		withDiff: function(diffs) {
			var html = [];
			for (var x = 0; x < diffs.length; x++) {
				var op = diffs[x][0]; // Operation (insert, delete, equal)
				var data = diffs[x][1]; // Text of change.
				var text = data.replace(pattern_amp, '&amp;').replace(pattern_lt,
						'&lt;').replace(pattern_gt, '&gt;').replace(pattern_para,
						'<br>');
				switch (op) {
				case DIFF_INSERT:
					html[x] = div_ins_start + text + div_end;
					break;
				case DIFF_DELETE:
					html[x] = div_delete_start + text + div_end;
					break;
				case DIFF_EQUAL:
					html[x] = '<span>' + text + '</span>';
					break;
				}
			}
			return html.join('');
		}
	};
})();
