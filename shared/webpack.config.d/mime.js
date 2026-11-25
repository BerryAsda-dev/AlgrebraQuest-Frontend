// Fuerza los tipos MIME correctos en el servidor de desarrollo:
//   .wasm -> application/wasm   (necesario para WebAssembly.instantiateStreaming)
//   .mjs  -> application/javascript
const forcedTypes = {
    wasm: "application/wasm",
    mjs: "application/javascript",
};

config.devServer = config.devServer || {};
config.devServer.devMiddleware = Object.assign({}, config.devServer.devMiddleware, {
    mimeTypes: Object.assign({}, (config.devServer.devMiddleware || {}).mimeTypes, forcedTypes),
});

const previousSetup = config.devServer.setupMiddlewares;
config.devServer.setupMiddlewares = function (middlewares, devServer) {
    middlewares.unshift({
        name: "force-wasm-mime",
        middleware: function (req, res, next) {
            const path = (req.url || "").split("?")[0];
            const ext = path.substring(path.lastIndexOf(".") + 1).toLowerCase();
            const forced = forcedTypes[ext];
            if (forced) {
                const original = res.setHeader.bind(res);
                res.setHeader = function (name, value) {
                    if (String(name).toLowerCase() === "content-type") value = forced;
                    return original(name, value);
                };
                original("Content-Type", forced);
            }
            next();
        },
    });
    return previousSetup ? previousSetup(middlewares, devServer) : middlewares;
};
