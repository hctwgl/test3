/**
 * Created by nizhiwei on 2017/4/14.
 */
var gulp = require('gulp'),
    clean = require('gulp-clean'),
    del = require('del'),
    babel = require('gulp-babel'),  //es6转码
    less = require('gulp-less'),
    cached = require('gulp-cached'), // 缓存未修改的文件，不多次编译
    uglify = require('gulp-uglify'),   //js压缩文件
    minifycss = require('gulp-minify-css'),// css压缩
    rename = require('gulp-rename'), // 重命名
    autoprefixer = require('gulp-autoprefixer'),// 添加 CSS 浏览器前缀
    plumber = require("gulp-plumber"),//出错打印日志不终止进程
    sourcemaps = require('gulp-sourcemaps'),//source-map
    gulpsync = require('gulp-sync')(gulp);
// clean 清空 dist 目录
gulp.task('clean', function(cb) {
    del([
        '51fbapi-web/src/main/webapp/dist/**/*'
    ],cb);
});
// es6编译为es5
gulp.task('es6', function() {
    return gulp.src(['51fbapi-web/src/main/webapp/build/js/**/*'])
        .pipe(sourcemaps.init())
        .pipe(plumber())
        .pipe(babel({presets: ['es2015']}))
        .pipe(cached('js'))
        .pipe(uglify())                  //压缩
        // .pipe(rename({suffix:".min"}))    //改名加前缀
        .pipe(sourcemaps.write('_srcmap'))
        .pipe(gulp.dest('51fbapi-web/src/main/webapp/dist/js'));
});
//less编译为css
gulp.task('less', function() {
    return gulp.src(['51fbapi-web/src/main/webapp/build/css/**/*'])
        .pipe(sourcemaps.init())
        .pipe(plumber())
        .pipe(less())
        .pipe(cached('less'))
        .pipe(autoprefixer('last 6 version'))
        .pipe(minifycss())
        .pipe(sourcemaps.write('_srcmap'))
        .pipe(gulp.dest('51fbapi-web/src/main/webapp/dist/css'));
});

// 监控 build 目录的改动自动编译
gulp.task('watch',function () {
    return gulp.watch('51fbapi-web/src/main/webapp/build/**/*', gulpsync.sync(['es6','less']));
});

// default 默认执行任务
gulp.task('default', ['clean','es6','less','watch']);