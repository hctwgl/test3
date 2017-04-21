/**
 * Created by nizhiwei on 2017/4/14.
 */
let gulp = require('gulp'),
    babel = require('gulp-babel'),  //es6转码
    less=require('gulp-less'),
    cached = require('gulp-cached'), // 缓存未修改的文件，不多次编译
    uglify = require('gulp-uglify'),   //js压缩文件
    rename = require('gulp-rename'), // 重命名
    autoprefixer = require('gulp-autoprefixer'),// 添加 CSS 浏览器前缀
    browserSync = require('browser-sync'), //浏览器自动刷新
    gulpsync = require('gulp-sync')(gulp);

// es6编译为es5
gulp.task('es6', function() {
    return gulp.src('51fbapi-web/build/js/**/*.js')
        .pipe(babel({presets: ['es2015']}))
        .pipe(cached('js'))
        // .pipe(uglify())                  //压缩
        //.pipe(rename({suffix:".min"}))    //改名加前缀
        .pipe(gulp.dest('51fbapi-web/src/main/webapp/js'));
});
//less编译为css
gulp.task('less', function() {
    return gulp.src('51fbapi-web/build/less/**/*.less')
        .pipe(less())
        .pipe(cached('less'))
        // .pipe(autoprefixer('last 6 version'))
        .pipe(gulp.dest('51fbapi-web/src/main/webapp/css'));
});
// styleReload （结合 watch 任务，无刷新CSS注入）
gulp.task('styleReload', ['less'], function() {
    return gulp.src(['build/less/**/*'])
        .pipe(cached('style'))
        .pipe(browserSync.reload({stream: true})); // 使用无刷新 browserSync 注入 CSS
});
// 监控 build 目录的改动自动编译
gulp.task('watch',function () {
    browserSync.init({
        server: {
            baseDir: 'build' // 在 dist 目录下启动本地服务器环境，自动启动默认浏览器
        }
    });
    gulp.watch('51fbapi-web/build/**/*', gulpsync.sync(['es6','less']));
    gulp.watch('build/less/**/*', ['styleReload']);// 监控 less 文件，有变动则执行CSS注入页面
    gulp.watch(["build/**/*", "!build/less/**/*"]).on('change',browserSync.reload); // 监控 build 目录下除 css 目录以外的变动（目前只有js），则自动刷新页面
});
