var loader={
    spinner: null,
    init: function(){
        var $wrapper = $('<div></div>').attr({
            "id": "loader-wrapper"
        }).css({
            "position": "absolute",
            "top": "50%",
            "left": "50%"
        });
        $('body').append($wrapper);
        var spinner = new CanvasLoader('loader-wrapper');
        spinner.setColor('#FFFFFF');
        spinner.setDiameter(62);
        spinner.setDensity(160);
        spinner.setRange(0.6);
        spinner.setSpeed(6);
        spinner.setFPS(60);
        this.spinner = spinner;
    },
    show: function(){
        if(this.spinner == null){
            this.init();
        }
        this.spinner.show();
    },
    hide: function(){
         if(this.spinner == null){
            return;
         }
         this.spinner.hide();
    }
}