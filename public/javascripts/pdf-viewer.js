var pdfViewer = {
    addPage: function(viewer, pdf, pageNumber, callback){
        pdf.getPage(pageNumber).then(function(page) {
            var viewport = page.getViewport($(viewer).width() / page.getViewport(1.0).width);
            var pageDisplayWidth = viewport.width;
            var pageDisplayHeight = viewport.height;
            var canvas = $('<canvas></canvas>').get(0);
            var context = canvas.getContext('2d');
            canvas.width = pageDisplayWidth;
            canvas.height = pageDisplayHeight;
            $(viewer).append(canvas);
            var renderContext = {
                canvasContext: context,
                viewport: viewport
            };
            page.render(renderContext).then(callback);
        });
    },
    render: function(pdfURL){
        var self = this;
        loader.show();
        PDFJS.getDocument(pdfURL).then(function (pdf) {
            loader.hide();
            var viewer = $('#viewer');
            var pageNumber = 1;
            self.addPage(viewer, pdf, pageNumber++, function pageRenderingComplete() {
                if (pageNumber > pdf.numPages){
                    return;
                }
                self.addPage(viewer, pdf, pageNumber++, pageRenderingComplete);
            });
        });
    }
}