var pdfViewer = {
    addPage: function(viewer, pdf, scale, pageNumber, callback){
        pdf.getPage(pageNumber).then(function(page) {
            var viewport = page.getViewport(scale);
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
    render: function(pdfURL, scale){
        var self = this;
        PDFJS.getDocument(pdfURL).then(function (pdf) {
            var viewer = $('#viewer');
            var pageNumber = 1;
            self.addPage(viewer, pdf, scale, pageNumber++, function pageRenderingComplete() {
                if (pageNumber > pdf.numPages){
                    return;
                }
                self.addPage(viewer, pdf, scale, pageNumber++, pageRenderingComplete);
            });
        });
    }
}