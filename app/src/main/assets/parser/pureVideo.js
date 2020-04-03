javascript:window.customScript.getHtml('==========start===========');
var child = document.children;
window.customScript.getHtml('==========child size : ==========='+child.length);
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
window.customScript.getHtml('==========arr size : ==========='+arr.length);
for(var i=0;i<arr.length;i++){
    var tagName = arr[i].tagName;
    var id = arr[i].id;
    var playerDiv = document.getElementsByClassName('player')[0];
    if(tagName.indexOf('HTML') != -1 || tagName.indexOf('BODY') != -1
    || (tagName.indexOf('HEAD') != -1 && tagName.indexOf('HEADER') == -1)
    || id.indexOf('mobile-index') != -1 || arr[i].parentNode == playerDiv){
        window.customScript.getHtml('========== jump index : ==========='+i);
        continue;
    }
    var className = arr[i].className;
    if(className == '' || (className.indexOf('player') == -1 && className.indexOf('playbox') == -1)
        || className.indexOf('-') != -1){
            window.customScript.getHtml('==========remove element i : ==========='+i);
            arr[i].parentNode.removeChild(arr[i]);
        }
}
window.customScript.getHtml('==========finally --> arr size : ==========='+arr.length);
var html = document.getElementsByTagName('html')[0];
window.customScript.getHtml('html : '+html.innerHTML);
//if(className == '' || (className.indexOf('player') == -1 && className.indexOf('playbox') == -1)
//    || className.indexOf('-') != -1){
//        window.customScript.getHtml('==========remove element i : ==========='+i);
//        arr[i].parentNode.removeChild(arr[i]);
//    }
