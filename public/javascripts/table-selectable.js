(function($)
{
    $.fn.tableSelectable=function(params)
    {
        this.each(function()
        {
            var $table = $(this);
            $table.on('click', 'tr.selectable',function(e){
                var $tr = $(this);
                if(e.target.nodeName != "A"){
                    if(e.target.nodeName != "INPUT"){
                        var $checkbox = $tr.find('td').first().find('input[type=checkbox]');
                        $checkbox.prop("checked", !$checkbox.prop("checked"));
                    }
                    $tr.toggleClass("selected");
                    var $checked = $table.find('input[type=checkbox]:checked');


                    var $onSelectMultipleButton = $('.on-select-multiple');
                    var $onSelectSingleButton = $('.on-select-single');

                    if($checked.length > 0){
                        $onSelectMultipleButton.removeClass("disabled");
                    }else if(!$onSelectMultipleButton.hasClass('disabled')){
                        $onSelectMultipleButton.addClass("disabled");
                    }

                    if($checked.length == 1){
                        $onSelectSingleButton.removeClass("disabled");
                    }else if(!$onSelectSingleButton.hasClass('disabled')){
                        $onSelectSingleButton.addClass("disabled");
                    }
                }
            });
        });

        return this;
    };
})(jQuery);

$(function(){
    $("table.table-selectable").tableSelectable();
});