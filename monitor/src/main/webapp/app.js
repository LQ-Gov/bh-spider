
var webpack_dev_middleware = require("webpack-dev-middleware");

var webpack_hot_middleware = require('webpack-hot-middleware');

var webpack_dev_server = require('webpack-dev-server');

//var formatWebpackMessages = require('react-dev-utils/formatWebpackMessages');


var config = require("./webpack.config.js");

var webpack = require("webpack");


const DEFAULT_PORT = parseInt(process.env.PORT, 10) || 3000;
const HOST = process.env.HOST || '0.0.0.0';


var compiler = webpack(config);

var app = new webpack_dev_server(compiler,{
    historyApiFallback:true,
    stats: { colors: true }
})

app.listen(8080);
// app.use(webpack_dev_middleware(compiler));

// app.use(webpack_hot_middleware(compiler));

// app.listen(3000, '127.0.0.1', function(err) {
//     if (err) {
//         console.log(err);
//         return;
//     }
//     console.log('server runing, listening at http://127.0.0.1:3000');
// });