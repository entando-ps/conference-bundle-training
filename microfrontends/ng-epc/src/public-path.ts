declare var __webpack_public_path__: string;
declare var process: any
if (process.env.NODE_ENV === 'production') {
    let publicpath = window.entando?.epc['ng-epc']?.basePath;
    if (publicpath && publicpath.slice(-1) !== '/') {
        publicpath = `${publicpath}/`;
    }
    // eslint-disable-next-line no-undef
    __webpack_public_path__ = publicpath || './';
}