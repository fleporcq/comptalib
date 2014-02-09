$(function(){
    $('body').on('keypress', function(e){
        if(!$(e.target).is(':input')){
            var code = (e.keyCode ? e.keyCode : e.which);

            if(code == 43) {
                $('#add-row-button').trigger('click');
            }
            if(code == 114) {
                $button = $('#recipe-button');
                if($button.length == 1){
                    location.href = $button.attr("href");
                }
            }
            if(code == 100) {
                $button = $('#expense-button');
                if($button.length == 1){
                    location.href = $button.attr("href");
                }
            }

        }

    });
});