  var lineDiff = function(text1, text2) {
	  console.log('text1 : ' + text1 + ' ' + 'text2 : ' + text2);
	  var dmp = new diff_match_patch();
	  var a = dmp.diff_linesToChars_(text2, text1);
	  var lineText1 = a['chars1'];
	  var lineText2 = a['chars2'];
	  var lineArray = a['lineArray'];

	  var diffs = dmp.diff_main(lineText1, lineText2, false);

	  dmp.diff_charsToLines_(diffs, lineArray);
	  var prettyHtml = createPrettyHTML(diffs);
	  dmp.diff_cleanupSemantic(diffs);
	  console.log('prettyHTML : ' + prettyHtml);
	  //prettyHtml = prettyHtml.replace('<')
	  return prettyHtml;
	}
  
 var createPrettyHTML = function(diffs) {
	  var html = [];
	  var pattern_amp = /&/g;
	  var pattern_lt = /</g;
	  var pattern_gt = />/g;
	  var pattern_para = /\n/g;
	  for (var x = 0; x < diffs.length; x++) {
	    var op = diffs[x][0];    // Operation (insert, delete, equal)
	    var data = diffs[x][1];  // Text of change.
	    var text = data.replace(pattern_amp, '&amp;').replace(pattern_lt, '&lt;')
	        .replace(pattern_gt, '&gt;').replace(pattern_para, '<br>');
	    switch (op) {
	      case DIFF_INSERT:
	        html[x] = '<div style="color:green">' + text + '</div>';
	        break;
	      case DIFF_DELETE:
	        html[x] = '<div style="color:red">' + text + '</div>';
	        break;
	      case DIFF_EQUAL:
	        html[x] = '<span>' + text + '</span>';
	        break;
	    }
	  }
	  return html.join('');
	  };
  