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
       $('#accountingRow-modal').find('.modal-header').find('h3').text($(this).attr('data-modal-title'));
       $('#accountingRow-modal').find('.modal-body').load($(this).attr('href') + ' #accountingRow-form',function(){
           $('#accountingRow-modal').find('#accountingRow-form').find('.form-actions').remove();
           $('#accountingRow-modal').modal();
       });
   });

    $('table.accounting').on('click', '.edit-row-link', function(e){
        e.preventDefault();
        $('#accountingRow-modal').find('.modal-header').find('h3').text($(this).attr('data-modal-title'));
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
                    $('table.accounting').load(location.href + ' table.accounting');
                    $('#accountingRow-modal').find('#accountingRow-form').find(':input[type!=hidden]').first().focus();
                    $('#delete-row-button').addClass("disabled");
                    $('#edit-row-button').addClass("disabled");
                }else{
                    $('#accountingRow-modal').find('#accountingRow-form').find('.control-group.error').first().find(':input').focus();
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

    $('#edit-row-button').on('click', function(e){
        e.preventDefault();
        if(!$(this).hasClass('disabled')){
            var $checked = $('table.accounting').find('input[type=checkbox]:checked');
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
                $('table.accounting').html($table.html());
                $('#delete-row-button').addClass("disabled");
                $('#edit-row-button').addClass("disabled");
            }
        });
    });


    $('#accountingRow-modal').on('keyup', 'input#totalAmount, input#personalWithdrawal',function(){
        computePercentage();
        toggleCategories();
    });


    $('table.accounting').on('click', 'tr.accounting-row',function(e){
        if(e.target.nodeName != "A"){
            if(e.target.nodeName != "INPUT"){
                var $checkbox = $(this).find('td').first().find('input[type=checkbox]');
                $checkbox.prop("checked", !$checkbox.prop("checked"));
            }
            $(this).toggleClass("selected");
             var $checked = $('table.accounting').find('input[type=checkbox]:checked');
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
        }
    });



});