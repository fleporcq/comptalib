(function($)
{
    $.fn.fixeHeader=function(params)
    {
        this.each(function()
        {
            var $table = $(this);
            var $thead = $table.find("thead");
            var $ths = $thead.find("th");
            $ths.each(function(i, th){
                var $th = $(th);
                var width = $th.width();
                $th.css({
                    "min-width":width,
                    "max-width":width,
                    "width":width
                });
            });
            var $theadClone = $thead.clone();

            $theadClone.css({
                "position":"fixed",
                "top":$thead.position().top
            });

            $table.prepend($theadClone);

            $(window).scroll(function(){
                $theadClone.css("left","-"+$(window).scrollLeft()+"px");
            });
        });

        return this;
    };
})(jQuery);

$(function(){
    $("table.table-fixed-header").fixeHeader();
});