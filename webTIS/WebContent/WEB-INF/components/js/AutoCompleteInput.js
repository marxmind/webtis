function AutoCompleteInput(clientID, ajaxDelay)
{
var labelInput = $("#" + clientID + "-labelInput");
var valueInput = $("#" + clientID + "-valueInput");
var matchesList = $("#" + clientID + "-matches");
var matchesCount = 0;

labelInput.each(function(index, input) {

    var onkeyup = input.onkeyup;
    input.onkeyup = null;

    $(input).on("keydown", function(e){                 
        var key;
        if (typeof e.which != "undefined"){
            key = e.which;
        }
        else{
            key = e.keyCode;
        }   

        if (key == 40){ // If down, set focus on listbox
            e.preventDefault();
            matchesList.focus()
        }
        else if(key != 37 && key != 38 && key != 39){ // Reset the value on any non-directional key to prevent the user from having an invalid label but still submitting a valid value.
            valueInput.val('') 
        }
    });     

    $(input).on("keyup", function(e) {

        var key;
        if (typeof e.which != "undefined"){
            key = e.which;
        }
        else{
            key = e.keyCode;
        }   

        if (key == 37 || key == 38 || key == 39){ // If left, right, or up, do not perform the search
            return false;
        }

        else{ // Otherwise, delay the ajax request by the specified time
            delay(function() { onkeyup.call(input, e); }, ajaxDelay);
        }

    });
});

var delay = (function() {
    var timer = 0;

    return function(callback, timeout) {

        clearTimeout(timer);
        timer = setTimeout(callback, timeout);
    };
})();

this.inputKeyup = function(strict)
{   
    labelInput = $("#" + clientID + "-labelInput");
    matchesList = $("#" + clientID + "-matches");
    matchesCount = matchesList.children("option").length;   
    if (matchesCount > 1 && labelInput.val().length > 0){
        matchesList.show();
    }
    else if (matchesCount == 1 && strict){
        var item = matchesList.children("option").get(0);
        selectItem(item.text, item.value);
        setTimeout(function(){matchesList.fadeOut()}, 500)

    }
    else{
        matchesList.hide();
    }           
}


this.listboxKeypress = function(input)
{
    $(input).on("keydown", function(e){
        var key;
        if (typeof e.which != "undefined"){
            key = e.which;
        }
        else{
            key = e.keyCode;
        }   
        if (key == 13){ // If enter, then select the item
            e.preventDefault();
            var label = $(input).find('option:selected').text();
            var value = $(input).val();
            selectItem(label, value);   
            matchesList.hide();
        }           
    });     
}

this.listboxClick = function(input)
{
    var label = $(input).find('option:selected').text();
    var value = $(input).val();
    selectItem(label, value);   
    matchesList.hide();
}

this.showMatches = function()
{
    if (typeof matchesList != "undefined" && matchesCount !=0 && labelInput.val().length > 0){
        matchesList.show();
    }
}

this.checkFocus = function(strict)
{
    setTimeout(function () {
        if (document.activeElement.className.indexOf("focusable") == -1 &&
            typeof matchesList != "undefined"){
            matchesList.hide();
        }
    }, 100);

    if (strict == false){
        valueInput.val(labelInput.val());
    }
}

function selectItem(itemLab, itemVal)
{

    labelInput.val(itemLab);
    valueInput.val(itemVal);
}

}