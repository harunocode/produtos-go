/*
 * Produtos GO — demonstração original organizada.
 *
 * ATENÇÃO:
 * Este arquivo mantém os dados simulados e a lógica original para que a
 * demonstração continue funcionando sem back-end. Não é a integração REST.
 * A migração deve substituir os dados SHOPS/favs/saved e os handlers de
 * autenticação/produtos por chamadas aos serviços em ../services/.
 *
 * Módulos futuros:
 *   services/produtoService.js
 *   services/lojaService.js
 *   services/authService.js
 */
var CATS=[
  ["todos","Todos","🗺️"],
  ["padaria","Padaria","🥖"],
  ["mercado","Mercado","🛒"],
  ["ferragem","Ferragem","🛠️"],
  ["bazar","Bazar","🏷️"],
  ["papelaria","Papelaria","✏️"],
  ["hortifruti","Hortifrúti","🍏"],
  ["petshop","Pet Shop","🐶"],
  ["farmacia","Farmácia","💊"],
  ["salao","Salão de Beleza","💇"],
  ["oficina","Oficina Mecânica","🔧"],
  ["roupas","Loja de Roupas","👕"]
];


var SHOPS=[

{n:"Ferragem do seu Zé",c:"ferragem",km:2,r:3,x:36,y:40,i:[["Chave de fenda",19.9,"🪛"],["Martelo",39.9,"🔨"],["Furadeira",189,"🔧"]]},

{n:"Casa do Parafuso",c:"ferragem",km:1,r:4,x:62,y:62,i:[["Chave de fenda",24.5,"🪛"],["Alicate",29.9,"🔧"]]},

{n:"Padaria Sabor do Trigo",c:"padaria",km:1.5,r:5,x:60,y:24,i:[["Pão francês (kg)",16.9,"🥖"],["Bolo de milho",18,"🍰"]]},

{n:"Mercado Estrela",c:"mercado",km:3,r:4,x:22,y:72,i:[["Arroz 5 kg",27.9,"🍚"],["Leite 1 L",5.49,"🥛"]]},

{n:"Bazar Alegria",c:"bazar",km:2.5,r:3,x:80,y:36,i:[["Jogo de copos",34.9,"🥤"],["Vassoura",22,"🧹"]]},

{n:"Papelaria Ponto",c:"papelaria",km:4,r:4,x:78,y:78,i:[["Caderno 96 folhas",16.9,"📓"],["Caneta (cx 12)",21,"🖊️"]]}];

var cat="todos",tiers={1:false,2:false,3:false},cur=null,hi=-1;

var $=function(i){return document.getElementById(i)};

function money(p){return"R$"+p.toFixed(2).replace(".",",")}

function tier(p){return p<20?1:p<100?2:3}

function stars(n){return"★".repeat(n)+"☆".repeat(5-n)}

function art(it){

  if(it[0]==="Chave de fenda"){

    var g="";[[170,18,80],[138,12,62],[108,8,46]].forEach(function(s){g+='<g transform="translate('+s[0]+','+s[1]+') rotate(125)"><rect x="0" y="-9" width="44" height="18" rx="8" fill="#2fb464"/><rect x="10" y="-9" width="5" height="18" fill="#1a8a47"/><rect x="44" y="-2" width="'+s[2]+'" height="4" fill="#b8c2c9"/><rect x="'+(44+s[2])+'" y="-3" width="7" height="6" fill="#8d99a2"/></g>'});

    return'<svg viewBox="0 0 200 130" role="img" aria-label="Chaves de fenda">'+g+'</svg>';

  }

  return it[2];

}

var cats=$("cats");

CATS.forEach(function(c){var b=document.createElement("button");b.className="chip";b.textContent=c[2]+" "+c[1];b.setAttribute("aria-pressed",c[0]==="todos");b.onclick=function(){cat=c[0];[].forEach.call(cats.children,function(x,j){x.setAttribute("aria-pressed",CATS[j][0]===cat)});render()};cats.appendChild(b)});

var prices=$("prices");

["$","$$","$$$"].forEach(function(t,j){var b=document.createElement("button");b.className="chip";b.textContent=t;b.setAttribute("aria-pressed","false");b.onclick=function(){tiers[j+1]=!tiers[j+1];b.setAttribute("aria-pressed",tiers[j+1]);render()};prices.appendChild(b)});

function anyTier(){return tiers[1]||tiers[2]||tiers[3]}

function card(s,it,si,ii){

  var b=document.createElement("button");b.className="card";b.dataset.s=si;

  b.innerHTML='<div class="art">'+art(it)+'</div><div class="cb"><b>'+esc(it[0])+'</b><small>'+esc(s.n)+'</small><div class="meta"><span class="price">'+money(it[1])+'</span><small>'+String(s.km).replace(".",",")+' km</small></div></div>';

  b.onclick=function(){open(si,ii)};return b;

}

function render(){

  var rad=parseFloat($("r").value),t=$("q").value.trim().toLowerCase();

  $("rv").textContent=String(rad).replace(".",",")+" km";

  $("circ").setAttribute("r",rad/5*105);

  [].forEach.call($("map").querySelectorAll(".pin"),function(p){p.remove()});

  var g=$("grid");g.innerHTML="";var shown={};

  var res=[];

  SHOPS.forEach(function(s,si){

    if(s.km>rad||(cat!=="todos"&&s.c!==cat))return;

    s.i.forEach(function(it,ii){

      if(t&&(it[0]+" "+s.n).toLowerCase().indexOf(t)<0)return;

      if(anyTier()&&!tiers[tier(it[1])])return;

      res.push([si,ii]);

    });

  });

  res.sort(function(a,b){return SHOPS[a[0]].km-SHOPS[b[0]].km});lastRes=res;

  if(!res.length){g.innerHTML='<div class="empty">Nenhum produto encontrado. Aumente o raio, troque a categoria ou limpe a busca.</div>';return}

  res.forEach(function(r){var s=SHOPS[r[0]];g.appendChild(card(s,s.i[r[1]],r[0],r[1]));

    if(!shown[r[0]]){shown[r[0]]=1;var p=document.createElement("button");p.className="pin";p.style.left=s.x+"%";p.style.top=s.y+"%";p.textContent=CATS.filter(function(c){return c[0]===s.c})[0][2];p.setAttribute("aria-label",s.n);p.dataset.s=r[0];

      p.onclick=function(){mark(r[0],true)};$("map").appendChild(p)}});

}

function mark(si,scroll){

  [].forEach.call(document.querySelectorAll(".pin,.card"),function(e){e.classList.toggle("on",+e.dataset.s===si)});

  if(scroll){var c=document.querySelector(".card.on");if(c)c.scrollIntoView({block:"nearest",behavior:"smooth"})}

}

function open(si,ii){

  cur=[si,ii];var s=SHOPS[si],it=s.i[ii];

  $("dArt").innerHTML=art(it);$("dStore").textContent=s.n;

  $("dStars").textContent=stars(s.r);$("dStars").setAttribute("aria-label",s.r+" de 5 estrelas");

  $("dDist").textContent="Distância "+String(s.km).replace(".",",")+" km 📍";

  $("dName").textContent=it[0];$("dPrice").textContent=money(it[1]);

  $("route").href="https://www.google.com/maps/search/?api=1&query="+encodeURIComponent(s.n+" Canoas RS");

  $("sv").setAttribute("aria-pressed","false");$("fv").setAttribute("aria-pressed","false");$("fv").textContent="🤍 Favoritar";$("toast").textContent="";

  var m=$("more");m.innerHTML="";s.i.forEach(function(o,j){if(j!==ii)m.appendChild(card(s,o,si,j))});

  $("vBusca").hidden=true;$("vProd").hidden=false;location.hash="produto";window.scrollTo(0,0);

}

function showBusca(){if(user&&user.tipo==='CLIENTE')goTab(lastTab)}

$("back").onclick=function(){location.hash="busca";showBusca()};

window.addEventListener("hashchange",function(){if(location.hash!=="#produto"||!cur)showBusca()});

$("sv").onclick=function(){var on=$("sv").getAttribute("aria-pressed")!=="true";$("sv").setAttribute("aria-pressed",on);$("toast").textContent=on?"Produto salvo na sua lista.":"Produto removido da lista."};

$("fv").onclick=function(){var on=$("fv").getAttribute("aria-pressed")!=="true";$("fv").setAttribute("aria-pressed",on);$("fv").textContent=on?"❤️ Favoritado":"🤍 Favoritar";$("toast").textContent=on?"Loja adicionada aos favoritos.":"Loja removida dos favoritos."};

$("q").oninput=render;$("r").oninput=render;$("go").onclick=function(){showBusca();render()};

var user=null,loja=null,demoLoja=null,favs={},saved={},lastTab='busca',mode='in',tp='CLIENTE';

function esc(s){return String(s).replace(/[&<>"]/g,function(c){return {'&':'&amp;','<':'&lt;','>':'&gt;','"':'&quot;'}[c]})}

function hideAll(){['vLogin','vBusca','vProd','vLista','vLoja','bar'].forEach(function(i){$(i).hidden=true})}

function drawTabs(){var t=$('tabsEl');t.innerHTML='';if(!user||user.tipo!=='CLIENTE')return;[['busca','Buscar'],['fav','Favoritos'],['sav','Curtidos']].forEach(function(x){var b=document.createElement('button');b.className='tab';b.textContent=x[1];b.setAttribute('aria-selected',x[0]===lastTab);b.onclick=function(){goTab(x[0])};t.appendChild(b)})}

function goTab(t){lastTab=t;hideAll();drawTabs();if(t==='busca'){$('vBusca').hidden=false;$('bar').hidden=false;return}

 var g=$('lG'),n=0;g.innerHTML='';$('vLista').hidden=false;

 if(t==='fav'){$('lT').textContent='Suas lojas favoritas';SHOPS.forEach(function(s,si){if(favs[si])s.i.forEach(function(it,ii){g.appendChild(card(s,it,si,ii));n++})})}

 else{$('lT').textContent='Produtos curtidos';Object.keys(saved).forEach(function(k){if(!saved[k])return;var si=+k.split('|')[0],nm=k.slice(k.indexOf('|')+1),s=SHOPS[si],ii=s?s.i.findIndex(function(it){return it[0]===nm}):-1;if(ii>-1){g.appendChild(card(s,s.i[ii],si,ii));n++}})}

 if(!n)g.innerHTML='<div class=empty>'+(t==='fav'?'Você ainda não favoritou nenhuma loja. Abra um produto e toque em Favoritar.':'Nada curtido ainda. Abra um produto e arraste para a direita.')+'</div>'}

function mkDemo(){if(!demoLoja)demoLoja={nome:SHOPS[0].n,status:'ATIVO',si:0,meus:SHOPS[0].i.map(function(it){return{n:it[0],p:it[1],e:it[2],est:8,on:true}})};return demoLoja}

function syncLoja(){if(loja.si!==null)SHOPS[loja.si].i=loja.meus.filter(function(p){return p.on&&p.est>0}).map(function(p){return[p.n,p.p,p.e]});renderLoja()}

function renderLoja(){hideAll();$('vLoja').hidden=false;$('lojaT').textContent='Painel de '+loja.nome;

 var m=loja.meus,tb=$('mb');tb.innerHTML='';$('me').hidden=m.length>0;

 m.forEach(function(p){var tr=document.createElement('tr');

  tr.innerHTML='<td>'+p.e+' '+esc(p.n)+(p.est===0?' <span class=badge>Sem estoque</span>':'')+(p.on?'':' <span class=badge>Pausado</span>')+'</td><td class=num>'+money(p.p)+'</td><td class=num><button class=mini aria-label="Diminuir estoque">−</button> '+p.est+' <button class=mini aria-label="Aumentar estoque">+</button></td><td><button class="mini w">'+(p.on?'Pausar':'Ativar')+'</button></td>';

  var b=tr.querySelectorAll('button');

  b[0].onclick=function(){if(p.est>0){p.est--;syncLoja()}};b[1].onclick=function(){p.est++;syncLoja()};b[2].onclick=function(){p.on=!p.on;syncLoja()};tb.appendChild(tr)});

 var R=loja.si===0?[[3,'Atendimento bom, mas a entrega demorou.'],[4,'Preço justo e loja organizada.'],[2,'Faltou a chave de fenda média.']]:[];

 var avg=R.length?R.reduce(function(a,r){return a+r[0]},0)/R.length:0;

 $('rv2').innerHTML=R.length?'<div><b>Nota média '+avg.toFixed(1).replace('.',',')+'</b> <span class=stars>'+stars(Math.round(avg))+'</span></div>'+R.map(function(r){return'<div class=rev><span class=stars>'+stars(r[0])+'</span><br>'+r[1]+'</div>'}).join(''):'<div class=empty>Ainda sem avaliações.</div>';

 $('lNote').textContent=loja.status==='ATIVO'?'Produtos ativos e com estoque aparecem na busca dos clientes.':'Seu cadastro está em análise. Seus produtos só aparecem na busca depois da aprovação.'}

function fields(){var up=mode==='up',lj=up&&tp==='ESTABELECIMENTO';

 $('mIn').setAttribute('aria-pressed',!up);$('mUp').setAttribute('aria-pressed',up);$('tC').setAttribute('aria-pressed',!lj);$('tL').setAttribute('aria-pressed',lj);

 $('fUp').hidden=!up;$('lNome').hidden=!up;['lLoja','lDoc','lCat','lCep'].forEach(function(i){$(i).hidden=!lj});

 $('enter').textContent=up?'Criar conta':'Entrar';$('erro').textContent='';$('pw').autocomplete=up?'new-password':'current-password'}

$('mIn').onclick=function(){mode='in';fields()};$('mUp').onclick=function(){mode='up';fields()};

$('tC').onclick=function(){tp='CLIENTE';fields()};$('tL').onclick=function(){tp='ESTABELECIMENTO';fields()};

$('enter').onclick=function(){var e=$('em').value.trim(),s=$('pw').value,up=mode==='up',tipo=up?tp:(/^loja/i.test(e)?'ESTABELECIMENTO':'CLIENTE'),lj=up&&tipo==='ESTABELECIMENTO',d=$('doc').value.replace(/\D/g,''),x=$('erro');

 function bad(m){x.textContent=m}

 if(!/^\S+@\S+\.\S+$/.test(e))return bad('Informe um e-mail válido.');

 if(s.length<6)return bad('A senha precisa ter pelo menos 6 caracteres.');

 if(up&&!$('nome').value.trim())return bad('Informe seu nome.');

 if(lj&&!$('lj').value.trim())return bad('Informe o nome da loja.');

 if(lj&&d.length!==11&&d.length!==14)return bad('Informe um CPF (11 dígitos) ou CNPJ (14 dígitos).');

 if(lj&&$('cep').value.replace(/\D/g,'').length!==8)return bad('Informe um CEP com 8 dígitos.');

 var nome=up?$('nome').value.trim():e.split('@')[0];

 user={nome:nome,tipo:tipo};

 loja=tipo==='ESTABELECIMENTO'?(lj?{nome:$('lj').value.trim(),status:'PENDENTE',si:null,meus:[]}:mkDemo()):null;

 x.textContent='';$('pw').value='';$('topo').hidden=false;$('whoN').textContent='Olá, '+nome;

 var b=$('whoB');b.className='badge'+(loja?(loja.status==='ATIVO'?' ok':' wait'):'');b.textContent=loja?(loja.status==='ATIVO'?'Loja ativa':'Aguardando aprovação'):'Cliente';

 if(loja){renderLoja()}else{lastTab='busca';goTab('busca');render()}

 location.hash=loja?'loja':'busca'};

$('out').onclick=function(){user=null;loja=null;cur=null;hideAll();$('topo').hidden=true;$('vLogin').hidden=false;mode='in';fields();location.hash='login'};

$('pa').onclick=function(){var n=$('pn').value.trim(),p=parseFloat($('pp').value),e=parseInt($('pe').value,10),x=$('pErr');

 if(!n){x.textContent='Informe o nome do produto.';return}if(!(p>=0)){x.textContent='Informe um preço válido.';return}if(!(e>=0)){x.textContent='O estoque não pode ser negativo.';return}

 loja.meus.push({n:n,p:p,e:'📦',est:e,on:true});$('pn').value='';$('pp').value='';$('pe').value=1;x.textContent='';syncLoja()};

function fkey(){return cur[0]+'|'+SHOPS[cur[0]].i[cur[1]][0]}

function refFav(){var f=!!favs[cur[0]],v=!!saved[fkey()];$('fv').setAttribute('aria-pressed',f);$('fv').textContent=f?'❤️ Favoritado':'🤍 Favoritar';$('sv').setAttribute('aria-pressed',v)}

var _o=open;open=function(si,ii){_o(si,ii);$('vLista').hidden=true;refFav()};

$('sv').onclick=function(){var k=fkey();saved[k]=!saved[k];refFav();$('toast').textContent=saved[k]?'Produto salvo na sua lista.':'Produto removido da lista.'};

$('fv').onclick=function(){favs[cur[0]]=!favs[cur[0]];refFav();$('toast').textContent=favs[cur[0]]?'Loja adicionada aos favoritos.':'Loja removida dos favoritos.'};

$('catSel').innerHTML=CATS.slice(1).map(function(c){return'<option>'+c[1]+'</option>'}).join('');fields();

var lastRes=[],deck=[],keep=false,busy=false,card0=document.querySelector('.big'),sx=0,dx=0,dragging=false;

function snack(m){var s=$('snack');s.textContent=m;s.classList.add('on');clearTimeout(snack.t);snack.t=setTimeout(function(){s.classList.remove('on')},1600)}

var _o3=open;open=function(si,ii){_o3(si,ii);var s=SHOPS[si],it=s.i[ii];

 if(!keep){var f=lastRes.some(function(r){return r[0]===si&&r[1]===ii});deck=f?lastRes.slice():s.i.map(function(_,j){return[si,j]})}

 keep=false;$('detail').hidden=false;$('dEnd').hidden=true;

 $('cNm').textContent=it[0];$('cPr').textContent=money(it[1]);$('cShop').textContent=s.n;$('cMeta').textContent=String(s.km).replace('.',',')+' km · '+stars(s.r)};

function nextCard(){var i=deck.findIndex(function(r){return r[0]===cur[0]&&r[1]===cur[1]});

 if(i<0||i+1>=deck.length){$('detail').hidden=true;$('dEnd').hidden=false;return}

 keep=true;open(deck[i+1][0],deck[i+1][1])}

function moveCard(x){card0.style.transform='translateX('+x+'px) rotate('+x/18+'deg)';$('stOk').style.opacity=Math.min(1,Math.max(0,x/90));$('stNo').style.opacity=Math.min(1,Math.max(0,-x/90))}

card0.addEventListener('pointerdown',function(e){if(busy||e.target.closest('button'))return;dragging=true;sx=e.clientX;dx=0;card0.classList.remove('fly');card0.classList.add('drag');card0.setPointerCapture(e.pointerId)});

card0.addEventListener('pointermove',function(e){if(!dragging)return;dx=e.clientX-sx;moveCard(dx)});

function endDrag(){if(!dragging)return;dragging=false;card0.classList.remove('drag');

 if(dx>100)swipe(1);else if(dx<-100)swipe(-1);else{card0.classList.add('fly');moveCard(0);setTimeout(function(){card0.classList.remove('fly')},330)}}

card0.addEventListener('pointerup',endDrag);card0.addEventListener('pointercancel',endDrag);

function swipe(dir){if(busy)return;busy=true;var k=fkey();

 card0.classList.add('fly');card0.style.transform='translateX('+dir*innerWidth+'px) rotate('+dir*30+'deg)';card0.style.opacity=0;

 if(dir>0){saved[k]=true;snack('Capturado! Produto curtido ❤️')}else snack('Passou 👋');

 setTimeout(function(){card0.classList.remove('fly');card0.style.transform='';card0.style.opacity='';$('stOk').style.opacity=0;$('stNo').style.opacity=0;busy=false;nextCard()},320)}

$('bNo').onclick=function(){swipe(-1)};$('bOk').onclick=function(){swipe(1)};$('dBack').onclick=function(){$('back').onclick()};

document.addEventListener('keydown',function(e){if($('vProd').hidden||$('detail').hidden||/INPUT|TEXTAREA|SELECT/.test(document.activeElement.tagName))return;if(e.key==='ArrowRight')swipe(1);if(e.key==='ArrowLeft')swipe(-1)});

render();
