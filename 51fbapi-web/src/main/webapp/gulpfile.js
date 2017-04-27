/**
 * Created by nizhiwei on 2017/4/14.
 */
var gulp = require('gulp'),
    del = require('del'),       //删除文件
    babel = require('gulp-babel'),  //es6转码
    less = require('gulp-less'),
   // rev = require('gulp-rev'),//写入md5值
   // revCollector  = require('gulp-rev-collector'),//版本控制
    cached = require('gulp-cached'), // 缓存未修改的文件，不多次编译
    uglify = require('gulp-uglify'),   //js压缩文件
    minifycss = require('gulp-minify-css'),// css压缩
    rename = require('gulp-rename'), // 重命名
    autoprefixer = require('gulp-autoprefixer'),// 添加 CSS 浏览器前缀
    plumber = require("gulp-plumber"),//出错打印日志不终止进程
    sourcemaps = require('gulp-sourcemaps'),//source-map
    sequence = require('gulp-sequence'),//顺序执行任务
    gulpsync = require('gulp-sync')(gulp);

// clean 清空 dist 目录
gulp.task('clean', function(cb) {
    return del(['dist'],cb);
});
// es6编译为es5
gulp.task('es6', function() {
    return gulp.src(['build/**/*.js'])
        .pipe(sourcemaps.init())
        .pipe(plumber())
        .pipe(babel({presets: ['es2015']}))
        .pipe(cached('js'))
        .pipe(uglify())                  //压缩
        // .pipe(rename({suffix:".min"}))    //改名加前缀
        .pipe(sourcemaps.write('_srcmap'))
       // .pipe(rev())
        .pipe(gulp.dest('dist'))
       // .pipe(rev.manifest())
       // .pipe(gulp.dest('dist/_srcmap/_rev/js'));
});
//less编译为css
gulp.task('less', function() {
    return gulp.src(['build/**/*.less','build/**/*.css'])
        .pipe(plumber())
        .pipe(less())
        .pipe(cached('less'))
        .pipe(autoprefixer('last 6 version'))
        .pipe(minifycss())
       // .pipe(rev())
        .pipe(gulp.dest('dist'))
       // .pipe(rev.manifest())
     //   .pipe(gulp.dest('dist/_srcmap/_rev/css'));
});
//版本控制
//gulp.task('add-version', function() {
 //   return gulp.src(['dist/_srcmap/_rev/**/*.json', 'WEB-INF/**/*.vm'])
  //      .pipe( revCollector({
  //          replaceReved: true
 //       }) )
//        .pipe( gulp.dest('WEB-INF/') );
//});
// 清理目录并重新编译
gulp.task('build',function (cb) {
    sequence('clean',['less','es6'])(cb)
});

// 监控 build 目录的改动自动编译
gulp.task('default',['build'],function () {
    return gulp.watch('build/**/*',gulpsync.sync(['es6','less']));
});
