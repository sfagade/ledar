/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

$(document).ready(function () {
    $(".required :input").each(function (evt) {

        var parent_div = $(this).parent();
        //console.log('div is: ' + $(parent_div).is(":visible"));
        if ($(parent_div).is(":visible")) { //we only want to validate fields that are showing
            $(this).attr('required', 'true');
            //console.log('setting attr');
        }
    });
});
