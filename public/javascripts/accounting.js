$(function(){
    $('#open-row-button').on('click', function(e){
        e.preventDefault();
        if(!$(this).hasClass('disabled')){
            var $checked = $('table.table-selectable').find('input[type=checkbox]:checked');
            if($checked.length == 1){
                location.href=$($checked.get(0)).parentsUntil("tr").parent().find("a.open-row-link").attr('href');
            }
        }
    });
});