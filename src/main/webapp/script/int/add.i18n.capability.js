/* This will initialize the i18n jquery plugin
   Known issues at this point
   a. Function/JS var doesn't work in IE7 because of the array.indexOf functionality - open bug out - should be fixed in next version
   b. Also in IE7 complex placeholder {0} logic doesn't work
   
   More reading : http://code.google.com/p/jquery-i18n-properties/
*/

jQuery.i18n.properties({
    name:'messages', 
    path:'/developer/resources/', 
    mode:'map',
    language: 'en'});