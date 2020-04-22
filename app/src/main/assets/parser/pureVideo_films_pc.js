javascript:window.customScript.log('==========start===========');
var imgs = document.getElementsByTagName('img');
window.customScript.log(" imgs size = "+imgs.length);
if(imgs.length > 0){
    for(var i=0;i<imgs.length;i++){
        if(imgs[i]){
            imgs[i].parentNode.removeChild(imgs[i]);
            window.customScript.log(" imgs remove  "+i);
        }
    }
}
var header = document.getElementsByClassName('header')[0];
if(header){
    header.parentNode.removeChild(header);
}
var headers_nav = document.getElementsByClassName('headers_nav')[0];
if(headers_nav){
    headers_nav.parentNode.removeChild(headers_nav);
}
var header_bg = document.getElementsByClassName('header_bg')[0];
if(header_bg){
    header_bg.parentNode.removeChild(header_bg);
}
var tab_a = document.getElementsByClassName('tab-a');
window.customScript.log(" tab_a size = "+tab_a.length);
if(tab_a.length > 0){
    for(var i=0;i<tab_a.length;i++){
        if(tab_a[i]){
            tab_a[i].parentNode.removeChild(tab_a[i]);
            window.customScript.log(" tab_a remove  "+i);
        }
    }
}
var playright = document.getElementById('playright');
if(playright){
    playright.parentNode.removeChild(playright);
}
var playtop = document.getElementById('playtop');
if(playtop){
    playtop.parentNode.removeChild(playtop);
}
var tab_r = document.getElementsByClassName('tab-r')[0];
if(tab_r){
    tab_r.parentNode.removeChild(tab_r);
}
var player_info = document.getElementsByClassName('player-info')[0];
if(player_info){
    player_info.parentNode.removeChild(player_info);
}
var els_sharebox = document.getElementsByClassName('els-sharebox')[0];
if(els_sharebox){
    els_sharebox.parentNode.removeChild(els_sharebox);
}
var player_vinfo = document.getElementsByClassName('player-vinfo')[0];
if(player_vinfo){
    player_vinfo.parentNode.removeChild(player_vinfo);
}
var vod_play = document.getElementsByClassName('vod-play')[0];
if(vod_play){
    vod_play.parentNode.removeChild(vod_play);
}
var vod_play = document.getElementsByClassName('vod-play')[0];
if(vod_play){
    vod_play.parentNode.removeChild(vod_play);
}
var footer = document.getElementsByClassName('footer')[0];
if(footer){
    footer.parentNode.removeChild(footer);
}
var vod_botx_title = document.getElementsByClassName('vod-botx-title')[0];
if(vod_botx_title){
    vod_botx_title.parentNode.removeChild(vod_botx_title);
}
var playopen = document.getElementsByClassName('playopen')[0];
if(playopen){
    playopen.parentNode.removeChild(playopen);
}
var html = document.getElementsByTagName('html')[0];
window.customScript.log('html : '+html.innerHTML);
window.customScript.onJSLoadComplete();