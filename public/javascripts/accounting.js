$(function(){



    var toggleCategories = function(){
        var amount = $('#add-row-modal').find('input#totalAmount').val();
        var personalWithdrawal = $('#add-row-modal').find('input#personalWithdrawal').val();
        var $category = $('#add-row-modal').find('select#categoryId');
        if($.isNumeric(amount) && $.isNumeric(personalWithdrawal) && Number(amount) <= Number(personalWithdrawal)){
            $category.val("").attr("disabled", "disabled").next(".help-inline").hide();
        }else{
            $category.removeAttr("disabled").next(".help-inline").show();
        }
    }

    var updatePercentageLabel = function(percentage){
        var $amountPercentageLabel = $('#add-row-modal').find('#amountPercentageLabel')
        if(percentage != null && percentage > 0){
            $amountPercentageLabel.text((percentage < 100 ? percentage : percentage > 100 ? ">" + 100 : 100) + " %");
        }else{
            $amountPercentageLabel.text("");
        }
    }

    var validateDecimal = function (value)    {
        var RE = /^\d*\.?\d{0,2}$/
        if(RE.test(value)){
           return true;
        }else{
           return false;
        }
    };

    var computePercentage = function(){
        var amount = $('#add-row-modal').find('input#totalAmount').val();
        var personalWithdrawal = $('#add-row-modal').find('input#personalWithdrawal').val();
        var $amountPercentage =  $('#add-row-modal').find('input#amountPercentage');
        if($.isNumeric(amount) && $.isNumeric(personalWithdrawal) && amount > 0){
            var percentage =  Number(personalWithdrawal / amount * 100).toFixed(1);
            $amountPercentage.slider('setValue', percentage);

            updatePercentageLabel(percentage);
        }else{
            $amountPercentage.slider('setValue', 0);

            updatePercentageLabel();
        }
        var $sliderHandle = $('#add-row-modal').find('.slider-handle');
        if($.isNumeric(amount) && amount > 0){
            $sliderHandle.removeClass("disabled");
        }else{
            $sliderHandle.addClass("disabled");
        }
    }

    var initSlider = function(){
        var amountPercentageSlider = $('#add-row-modal').find('#amountPercentage').slider({
            "min": 0,
            "max": 100,
            "step": 1,
            "value": 0,
            "tooltip": "hide"

        }).on('slide', function(e){
            var amount = $('#add-row-modal').find('input#totalAmount').val();
            var $personalWithdrawal = $('#add-row-modal').find('input#personalWithdrawal');
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
       $('#add-row-modal').find('.modal-body').load($(this).attr('href') + ' #accountingRow-form',function(){
           $('#add-row-modal').find('#accountingRow-form').find('.form-actions').remove();
           $('#add-row-modal').modal();
       });
   });

    $('#add-row-modal').on('shown', function () {
        $('#add-row-modal').find('#accountingRow-form').find(':input[type!=hidden]').first().focus();
    });
    $('#add-row-modal').on('show', function () {
        initSlider();
    });
    $('#add-row-modal').on('hidden', function () {
        $('#add-row-button').focus();
    });


    $('#add-row-modal').on('click', '#modal-save-button',function(){
        $('#accountingRow-form').submit();
    });

    $('body').on('keypress', function(e){
        if(!$(e.target).is(':input')){
            var code = (e.keyCode ? e.keyCode : e.which);
            if(code == 43) {
                $('#add-row-button').trigger('click');
            }
            if(code == 114) {
                location.href = $('#recipe-button').attr("href");
            }
            if(code == 101) {
                location.href = $('#expense-button').attr("href");
            }

        }

    });

    $('#add-row-modal').on('keypress', '#accountingRow-form',function(e){

        var code = (e.keyCode ? e.keyCode : e.which);
        if(code == 13) {
            e.preventDefault();
            $('#accountingRow-form').submit();
        }
    });

    $('#add-row-modal').on('submit','#accountingRow-form',function(e){
        e.preventDefault();
        $.ajax({
            url: $(this).attr('action'),
            type: $(this).attr('method'),
            data: $(this).serialize(),
            success: function(data) {
                var $holder = $('<div></div>').html(data);
                var $form = $holder.find('#accountingRow-form');
                $form.find('.form-actions').remove();
                $('#add-row-modal').find('.modal-body').html($form);
                initSlider();
                toggleCategories();
                if($form.find('.alert-success').length > 0){
                    $('table.accounting').load(location.href + ' table.accounting');
                    $('#add-row-modal').find('#accountingRow-form').find(':input[type!=hidden]').first().focus();
                }else{
                    $('#add-row-modal').find('#accountingRow-form').find('.control-group.error').first().find(':input').focus();
                }
            }
        });
    });

    $('#delete-row-button').on('click', function(e){
        e.preventDefault();
        if(!$(this).hasClass('disabled')){
            bootbox.confirm($(this).attr("data-message"), $(this).attr("data-cancel"), $(this).attr("data-confirm"), function(yes) {
              if(yes){
                $('#accountingRows-form').submit();
              }
            });
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
                $('table.accounting').html($table.html());
            }
        });
    });


    $('#add-row-modal').on('keyup', 'input#totalAmount, input#personalWithdrawal',function(){
        computePercentage();
        toggleCategories();
    });

    $('#add-row-modal').on('keypress', 'input#totalAmount, input#personalWithdrawal',function(e){
        var code = (e.keyCode ? e.keyCode : e.which);
        var char = String.fromCharCode(code);
        var value =   $(this).val() + char;
        if(!validateDecimal(value)){
            e.preventDefault();
        }

    });

    $('table.accounting').on('click', 'tr.accounting-row',function(e){
        $checkbox = $(this).find('td').first().find('input[type=checkbox]');
        if(e.target != $checkbox.get(0)){
            $checkbox.prop("checked", !$checkbox.prop("checked"));
        }
        $(this).toggleClass("selected");
         $checked = $('table.accounting').find('input[type=checkbox]:checked');
         var $deleteButton = $('#delete-row-button');
         var $editButton = $('#edit-row-button');

         if($checked.length > 0){
            $deleteButton.removeClass("disabled");
         }else if(!$deleteButton.hasClass('disabled')){
            $deleteButton.addClass("disabled");
         }

         if($checked.length == 1){
             $editButton.removeClass("disabled");
          }else if(!$editButton.hasClass('disabled')){
             $editButton.addClass("disabled");
          }
    });



});