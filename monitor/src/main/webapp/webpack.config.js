var path = require("path")

var webpack = require('webpack');

var html_webpack_plugin = require('html-webpack-plugin');

var webpack_dev_middleware = require("webpack-dev-middleware");

var open_browser_webpack_plugin = require("open-browser-webpack-plugin");

var extract_text_webpack_plugin = require("extract-text-webpack-plugin");


//var extract_text_webpack_plugin = require('extract-text-webpack-plugin');


module.exports = {
    entry: {
        index: ["babel-polyfill","./app-router.js"]
    },
    output: {
        path: path.join(__dirname, "public"),
        filename: "[name].js"
    },

    resolve: {
        extensions: ['.web.js', '.js', '.json', '.web.jsx', '.jsx'],
        alias: {
            jsx: path.join(__filename, "src")
        }
    },

    module: {
        loaders: [{
            test: /\.(js|jsx)$/,
            loader: require.resolve('babel-loader'),
            // query: {
            //     presets: ["react"]
            // },
            options: {
                plugins: [
                    ['import', {libraryName: 'antd', style: 'css'}],
                ],
                // This is a feature of `babel-loader` for webpack (not Babel itself).
                // It enables caching results in ./node_modules/.cache/babel-loader/
                // directory for faster rebuilds.
                cacheDirectory: true
            }
        },
            {
                test: /\.(css)$/,
                loader: extract_text_webpack_plugin.extract({fallback: 'style-loader', use: 'css-loader'})

            }

        ]
    },

    plugins: [
        new html_webpack_plugin({title: "TEST SPIDER APP", template: "src/index-template.html"}),
        // new webpack.optimize.OccurenceOrderPlugin(),
        //new webpack.HotModuleReplacementPlugin(),

        new open_browser_webpack_plugin({url: 'http://localhost:8080'}),

        new extract_text_webpack_plugin("./assets/css/[name].css")
        // new webpack.NoErrorsPlugin()
    ]
}



