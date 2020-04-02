javascript:var body = document.getElementsByTagName('html')[0];
var divs = body.getElementsByTagName('div');
for(int i=0;i<divs.length;i++){
    if(divs[i].className == "" || !divs[i].className.contains("playbox")){
        divs[i].parentNode.removeChild(divs[i]);
    }
}