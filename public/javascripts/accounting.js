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
    $('#delete-row-button').on('click', function(e){
        e.preventDefault();
        if(!$(this).hasClass('disabled')){
            bootbox.confirm(Messages("confirm.accounting.delete"), Messages("action.cancel"), Messages("action.confirm"), function(yes) {
                if(yes){
                    $('#accountings-form').submit();
                }
            });
        }
    });



    $('#accounting-modal').on('submit','#accounting-form',function(e){
        e.preventDefault();
        $.ajax({
            url: $(this).attr('action'),
            type: $(this).attr('method'),
            data: $(this).serialize(),
            success: function(data) {
                var $holder = $('<div></div>').html(data);
                var $form = $holder.find('#accounting-form');
                $form.find('.form-actions').remove();
                $('#accounting-modal').find('.modal-body').html($form);
                if($form.find('.alert-success').length > 0){
                    $('table.accountings').load(location.href + ' table.accountings >',function(){
                        $('table.accountings').fixeHeader();
                    });

                    $('#accounting-modal').find('#accounting-form').find(':input[type!=hidden]').first().focus();
                    $('.on-select-multiple,.on-select-single').addClass("disabled");
                }else{
                    $('#accounting-modal').find('#accounting-form').find('.control-group.error').first().find(':input').focus();
                }
            }
        });
    });

    $('#add-row-button').on('click', function(e){
        e.preventDefault();
        $('#accounting-modal').find('.modal-body').load($(this).attr('href') + ' #accounting-form',function(){
            $('#accounting-modal').find('#accounting-form').find('.form-actions').remove();
            $('#accounting-modal').modal();
        });
    });

    $('#accounting-modal').on('shown', function () {
        $('#accounting-modal').find('#accounting-form').find(':input[type!=hidden]').first().focus();
    });

    $('#accounting-modal').on('click', '#modal-save-button',function(){
        $('#accounting-form').submit();
    });
    $('#accountings-form').on('submit', function(e){
        e.preventDefault();
        $.ajax({
            url: $(this).attr('action'),
            type: $(this).attr('method'),
            data: $(this).serialize(),
            success: function(data) {
                var $holder = $('<div></div>').html(data);
                var $table = $holder.find('table.accountings');
                $('table.accountings').html($table.html()).fixeHeader();
                $('.on-select-multiple,.on-select-single').addClass("disabled");
            }
        });
    });
});