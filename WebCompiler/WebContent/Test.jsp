<!doctype jsp>
<html lang="en">
<head>
  <meta charset="utf-8">
  <title>jQuery UI Sortable - Drop placeholder</title>
  <link rel="stylesheet" href="./jQueryUI/jquery-ui.min.css">
  <script src="./jQueryUI/external/jquery/jquery.js"></script>
  <script src="./jQueryUI/jquery-ui.min.js"></script>
  <style>
  #sortable { list-style-type: none; margin: 100; padding: 100; width: 60%; }
  #sortable li { margin: 5px 5px 5px 5px; padding: 5px; font-size: 1.2em; height: 1.5em; }
  #sortable .ui-selecting { opacity: 0.4; border: 2px solid black;}
  #sortable .ui-selected { opacity: 1; border: 2px solid black; }
  #sortable .ui-state-ass {
		background: #adadad url("./images/ui-state-assign.png") 50% 50% repeat-x;
		font-weight: normal;
		color: #333333;
	}
  #sortable .ui-state-opr {
		background: #adadad url("./images/ui-state-opr.png") 50% 50% repeat-x;
		font-weight: normal;
		color: #333333;
	}
  #sortable .ui-state-loop {
		background: #adadad url("./images/ui-state-loop.png") 50% 50% repeat-x;
		font-weight: normal;
		color: #333333;
	}
  #sortable .ui-state-if {
		background: #adadad url("./images/ui-state-if1.png") 50% 50% repeat-x;
		color: #FFFFFF;
	}
  #sortable .ui-state-out {
		background: #adadad url("./images/ui-state-out.png") 50% 50% repeat-x;
		font-weight: normal;
		color: #333333;
	}

  </style>
  <script>
  $(function() {
    var decCount = 0,assCount = 0,oprCount=0,loopCount=0,ifCount=0,inputCount=0,outputCount=0;

    ///added function
    function SelectSelectableElement (selectableContainer, elementsToSelect)
    {
      // add unselecting class to all elements in the styleboard canvas except the ones to select
      $(".ui-selected", selectableContainer).not(elementsToSelect).removeClass("ui-selected").addClass("ui-unselecting");

      // add ui-selecting class to the elements to select
      $(elementsToSelect).not(".ui-selecting").addClass("ui-selected");
      $(elementsToSelect).not(".ui-selected").addClass("ui-selecting");

    }
    ///added function end

    $( "#sortable").sortable({
      placeholder: "ui-state-highlight",
    });

     $("#sortable").selectable({
/*       	 stop: function() {
	        var result = $( "#select-result" ).empty();
	        $( ".ui-selected", this ).each(function() {
	          var index = this.id;
	          result.append( " #" + index);
	        });
      	 } */
    });
     
    $("#b_result").on("click",function(){
    	$("#sortable li").each(function(){
          console.log($(this));
        });
    });

    $("body").bind('keydown',function(e){
      if ((e.which==8 || e.which == 46)&&e.target.id.substr(0,3)!="inp"){
   	  	$( ".ui-selected", this ).each(function() {
          $(this).remove();
       	});
        e.preventDefault();
      }
      if (e.which==39&&e.target.id.substr(0,3)!="inp"){
    	  $( ".ui-selected", this ).each(function() {
    		  var pl = parseInt($(this).css("marginLeft").replace(/[^-\d\.]/g, ''));
    		  if (pl==105) return true;
    		  pl=pl+20;
	    	  $(this).css("marginLeft",pl);
    	  });
    	  e.preventDefault();
      }
      if (e.which==37&&e.target.id.substr(0,3)!="inp"){
    	  $( ".ui-selected", this ).each(function() {
    		  var pl = parseInt($(this).css("marginLeft").replace(/[^-\d\.]/g, ''));
    		  if (pl==5) return true;
    		  pl=pl-20;
	    	  $(this).css("marginLeft",pl);
    	  });
    	  e.preventDefault();
      }
    });
    
    

    $( "#b_add_dec").bind("click",function(){  //addbtn eventlistener
      $( "#sortable").append('<li id="i_dec' + decCount + '" class="ui-state-default">Declaration'+ decCount +'  <select id="s_dec'+ decCount+'"><option value="char">char</option><option value="int">int</option><option value="float">float</option></select> <input maxlength="10" size="10" id="inp_dec'+decCount+'"><input title="If you want array, Check here" type="checkbox" id="chk_dec'+ decCount +'" name="chk_info" value="Array">Array  <input title="Array Size" id="inp_size_dec'+decCount+'" maxlength="4" size="4" disabled></li>');

      $( "#i_dec" + decCount).mousedown(function(){
        SelectSelectableElement($("#sortable"),$(this,"#sortable"));
      });
      
      $("#chk_dec"+decCount).change(function(){
    	  var tempid = this.id.substr(-1,1);
        if ($(this).is(":checked")){
          $("#inp_size_dec"+tempid).removeAttr("disabled");
        }else{
          $("#inp_size_dec"+tempid).attr("disabled", "disabled").val("");
        }
      });
      
      decCount++;  //id == item0,item1,item2....
    });
    
    $( "#b_add_operation").bind("click",function(){
    	$( "#sortable").append('<li id="i_opr' + oprCount + '" class="ui-state-opr">Operation'+ oprCount +'  <input maxlength="10" size="10" id="inp_dest_opr'+oprCount+'"> = <input maxlength="10" size="10" id="inp_left_opr'+oprCount+'">  <select id="s_opr'+ oprCount+'"><option value="add">+</option><option value="sub">-</option><option value="mul">*</option><option value="div">/</option></select>  <input maxlength="10" size="10" id="inp_right_opr'+oprCount+'"></li>');
    	
    	$( "#i_opr" + oprCount).mousedown(function(){
    		SelectSelectableElement($("#sortable"),$(this,"#sortable"));
    	});
    	
    	oprCount++;
    });
    
    $( "#b_add_ass").bind("click",function(){
    	$( "#sortable").append('<li id="i_ass' + assCount + '" class="ui-state-ass">Assignment'+ assCount +'  <input maxlength="10" size="10" id="inp_left_ass'+assCount+'"> = <input id="inp_right_ass'+assCount+'" maxlength="10" size="10"></li>');
    	
    	$( "#i_ass" + assCount).mousedown(function(){
    		SelectSelectableElement($("#sortable"),$(this,"#sortable"));
    	});
    	
    	assCount++;
    });
    
    $( "#b_add_loop").bind("click",function(){
    	$( "#sortable").append('<li id="i_loop' + loopCount + '" class="ui-state-loop">While'+ loopCount +'  <input maxlength="30" size="30" id="inp_loop'+loopCount+'"></li>');
    	
    	$( "#i_loop" + loopCount).mousedown(function(){
    		SelectSelectableElement($("#sortable"),$(this,"#sortable"));
    	});
    	
    	loopCount++;
    });
    
    $( "#b_add_if").bind("click",function(){
    	$( "#sortable").append('<li id="i_if' + ifCount + '" class="ui-state-if">If'+ ifCount +'  <input maxlength="30" size="30" id="inp_if'+ifCount+'"></li>');
    	
    	$( "#i_if" + ifCount).mousedown(function(){
    		SelectSelectableElement($("#sortable"),$(this,"#sortable"));
    	});
    	
    	ifCount++;
    });
    
    $( "#b_add_input").bind("click",function(){
    	$( "#sortable").append('<li id="i_input' + inputCount + '" class="ui-state-error">Input'+ inputCount +'  <select id="s_input'+ inputCount+'"><option value="char">char</option><option value="int">int</option><option value="float">float</option></select>  <input maxlength="30" size="30" id="inp_input'+inputCount+'"></li>');
    	
    	$( "#i_input" + inputCount).mousedown(function(){
    		SelectSelectableElement($("#sortable"),$(this,"#sortable"));
    	});
    	
    	inputCount++;
    });
    
	$( "#b_add_output").bind("click",function(){
		$( "#sortable").append('<li id="i_output' + outputCount + '" class="ui-state-out">Output'+ outputCount +'  <select id="s_output'+ outputCount+'"><option value="char">char</option><option value="int">int</option><option value="float">float</option></select>  <input maxlength="30" size="30" id="inp_output'+outputCount+'"></li>');
    	
    	$( "#i_output" + outputCount).mousedown(function(){
    		SelectSelectableElement($("#sortable"),$(this,"#sortable"));
    	});
    	
    	outputCount++;
    });
    
  });
  </script>
</head>
<body>
<button id="b_add_dec">Add Declaration</button> <button id="b_add_ass">Add Assignment</button> <button id="b_add_operation">Add Operation</button> <button id="b_add_loop">Add While</button> <button id="b_add_if">Add If</button> <button id="b_add_input">Add Input</button> <button id="b_add_output">Add Output</button> <button id="b_result">Result</button><br>
<ul id="sortable">
</ul>
</body>
</html>