javascript:window.customScript.log('==========start===========');
var child = document.children;
var arr = [];
function fn(obj){
    for(var i=0;i<obj.length;i++){
        if(obj[i].children){
            fn(obj[i].children);
        }
        arr.push(obj[i]);
    }
}
fn(child);
for(var i=0;i<arr.length;i++){
    var tagName = arr[i].tagName;
    var id = arr[i].id;
    var playerDiv = document.getElementsByClassName('player')[0];
    if(tagName.indexOf('HTML') != -1 || tagName.indexOf('BODY') != -1
    || (tagName.indexOf('HEAD') != -1 && tagName.indexOf('HEADER') == -1)
    || id.indexOf('mobile-index') != -1 || arr[i].parentNode == playerDiv){
        continue;
    }
    var className = arr[i].className;
    if(className == '' || (className.indexOf('player') == -1 && className.indexOf('playbox') == -1)
        || className.indexOf('-') != -1){
            arr[i].parentNode.removeChild(arr[i]);
        }
}
var html = document.getElementsByTagName('html')[0];
window.customScript.log('html : '+html.innerHTML);
window.customScript.onJSLoadComplete();
//var videoParent = document.getElementById('mobile-index');
//videoParent.setAttribute('style','position:fixed;align-items: \"center\";');