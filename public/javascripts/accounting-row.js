var accounting = {};
$(function(){

    var toggleCategories = function(){
        var amount = $('#accountingRow-modal').find('input#totalAmount').val();
        var personalWithdrawal = $('#accountingRow-modal').find('input#personalWithdrawal').val();
        var $category = $('#accountingRow-modal').find('select#categoryId');
        if($.isNumeric(amount) && $.isNumeric(personalWithdrawal) && Number(amount) <= Number(personalWithdrawal)){
            $category.attr("disabled", "disabled").next(".help-inline").hide();
        }else{
            $category.removeAttr("disabled").next(".help-inline").show();
        }
    }

    var updatePercentageLabel = function(percentage){
        var $amountPercentageLabel = $('#accountingRow-modal').find('#amountPercentageLabel')
        if(percentage != null && percentage > 0){
            $amountPercentageLabel.text((percentage < 100 ? percentage : percentage > 100 ? ">" + 100 : 100) + " %");
        }else{
            $amountPercentageLabel.text("");
        }
    }

    var computePercentage = function(){
        var amount = $('#accountingRow-modal').find('input#totalAmount').val();
        var personalWithdrawal = $('#accountingRow-modal').find('input#personalWithdrawal').val();
        var $amountPercentage =  $('#accountingRow-modal').find('input#amountPercentage');
        if($.isNumeric(amount) && $.isNumeric(personalWithdrawal) && amount > 0){
            var percentage =  Number(personalWithdrawal / amount * 100).toFixed(1);
            $amountPercentage.slider('setValue', percentage);

            updatePercentageLabel(percentage);
        }else{
            $amountPercentage.slider('setValue', 0);

            updatePercentageLabel();
        }
        var $sliderHandle = $('#accountingRow-modal').find('.slider-handle');
        if($.isNumeric(amount) && amount > 0){
            $sliderHandle.removeClass("disabled");
        }else{
            $sliderHandle.addClass("disabled");
        }
    }

    var initSlider = function(){
        var amountPercentageSlider = $('#accountingRow-modal').find('#amountPercentage').slider({
            "min": 0,
            "max": 100,
            "step": 1,
            "value": 0,
            "tooltip": "hide"

        }).on('slide', function(e){
            var amount = $('#accountingRow-modal').find('input#totalAmount').val();
            var $personalWithdrawal = $('#accountingRow-modal').find('input#personalWithdrawal');
            if($.isNumeric(amount) && amount > 0){
                var personalWithdrawal =  Number((amount  * amountPercentageSlider.getValue() / 100).toFixed(2));
                $personalWithdrawal.val(personalWithdrawal > 0 ? personalWithdrawal : "");
            }else{
                amountPercentageSlider.setValue(0);
            }
            updatePercentageLabel(amountPercentageSlider.getValue());
            toggleCategories();
        }).data('slider');
        computePercentage();

    }

   $('#add-row-button').on('click', function(e){
       e.preventDefault();
       var $title = $('#accountingRow-modal').find('.modal-header').find('h3');
       $('#accountingRow-modal').find('.modal-header').find('h3').text(accounting.month + " " + accounting.year + " - " + Messages("title.accounting.recipe.add"));
       $('#accountingRow-modal').find('.modal-body').load($(this).attr('href') + ' #accountingRow-form',function(){
           $('#accountingRow-modal').find('#accountingRow-form').find('.form-actions').remove();
           $('#accountingRow-modal').modal();
       });
   });

    $('body').on('click', '.edit-row-link', function(e){
        e.preventDefault();
        $('#accountingRow-modal').find('.modal-header').find('h3').text(accounting.month + " " + accounting.year + " - " + Messages("title.accounting.recipe.edit"));
        $('#accountingRow-modal').find('.modal-body').load($(this).attr('href') + ' #accountingRow-form',function(){
            $('#accountingRow-modal').find('#accountingRow-form').find('.form-actions').remove();
            toggleCategories();
            $('#accountingRow-modal').modal();
        });
    });


    $('#accountingRow-modal').on('shown', function () {
        $('#accountingRow-modal').find('#accountingRow-form').find(':input[type!=hidden]').first().focus();
    });
    $('#accountingRow-modal').on('show', function () {
        initSlider();
    });
    $('#accountingRow-modal').on('hidden', function () {
        $('#add-row-button').focus();
    });


    $('#accountingRow-modal').on('click', '#modal-save-button',function(){
        $('#accountingRow-form').submit();
    });

    $('#accountingRow-modal').on('keypress', '#accountingRow-form',function(e){

        var code = (e.keyCode ? e.keyCode : e.which);
        if(code == 13) {
            e.preventDefault();
            $('#accountingRow-form').submit();
        }
    });

    $('#accountingRow-modal').on('submit','#accountingRow-form',function(e){
        e.preventDefault();
        $.ajax({
            url: $(this).attr('action'),
            type: $(this).attr('method'),
            data: $(this).serialize(),
            success: function(data) {
                var $holder = $('<div></div>').html(data);
                var $form = $holder.find('#accountingRow-form');
                $form.find('.form-actions').remove();
                $('#accountingRow-modal').find('.modal-body').html($form);
                initSlider();
                toggleCategories();
                if($form.find('.alert-success').length > 0){
                    $('table.accounting').load(location.href + ' table.accounting >',function(){
                        $('table.accounting').fixeHeader();
                    });

                    $('#accountingRow-modal').find('#accountingRow-form').find(':input[type!=hidden]').first().focus();
                    $('.on-select-multiple,.on-select-single').addClass("disabled");
                }else{
                    $('#accountingRow-modal').find('#accountingRow-form').find('.control-group.error').first().find(':input').focus();
                }
            }
        });
    });

    $('#delete-row-button').on('click', function(e){
        e.preventDefault();
        if(!$(this).hasClass('disabled')){
            bootbox.confirm(Messages("confirm.accountingRows.delete"), Messages("action.cancel"), Messages("action.confirm"), function(yes) {
              if(yes){
                $('#accountingRows-form').submit();
              }
            });
        }
    });

    $('#edit-row-button').on('click', function(e){
        e.preventDefault();
        if(!$(this).hasClass('disabled')){
            var $checked = $('table.table-selectable').find('input[type=checkbox]:checked');
            if($checked.length == 1){
                $($checked.get(0)).parentsUntil("tr").parent().find("a.edit-row-link").trigger("click");
            }
        }
    });


    $('#accountingRows-form').on('submit', function(e){
        e.preventDefault();
        $.ajax({
            url: $(this).attr('action'),
            type: $(this).attr('method'),
            data: $(this).serialize(),
            success: function(data) {
                var $holder = $('<div></div>').html(data);
                var $table = $holder.find('table.accounting');
                $('table.accounting').html($table.html()).fixeHeader();
                $('.on-select-multiple,.on-select-single').addClass("disabled");
            }
        });
    });


    $('#accountingRow-modal').on('keyup', 'input#totalAmount, input#personalWithdrawal',function(){
        computePercentage();
        toggleCategories();
    });


});