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
    cleancss = require('gulp-clean-css'),// css压缩
    rename = require('gulp-rename'), // 重命名
    autoprefixer = require('gulp-autoprefixer'),// 添加 CSS 浏览器前缀
    plumber = require("gulp-plumber"),//出错打印日志不终止进程
    sourcemaps = require('gulp-sourcemaps'),//source-map
    sequence = require('gulp-sequence'),//顺序执行任务
    gulpsync = require('gulp-sync')(gulp),
    browserSync = require('browser-sync'),//浏览器自动刷新
    changed = require('gulp-changed'), //检查改变状态
    watch = require('gulp-watch'), //监听更改的文件
    livereload = require('gulp-livereload'),
    reload = browserSync.reload;

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
        .pipe(cleancss())
       // .pipe(rev())
        .pipe(gulp.dest('dist'))
        .pipe(reload({ stream:true }))
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
// 浏览器重载
gulp.task('js-watch', ['es6'], browserSync.reload);


gulp.task('watch', function() {
    livereload.listen()
    gulp.watch('build/**/*.*', function(event) {
        livereload.changed(event.path)
    })
    gulp.watch('WEB-INF/**/*.*', function(event) {
        livereload.changed(event.path)
    })
})

gulp.task('dev-es', function() {
    return gulp.src(['build/**/*.js'])
        .pipe(watch(['build/**/*.js'])) 
        .pipe(sourcemaps.init())
        .pipe(plumber())
        .pipe(babel({presets: ['es2015']}))
        .pipe(sourcemaps.write('_srcmap'))
        .pipe(gulp.dest('dist'))
        .pipe(reload({stream: true}))
})

gulp.task('dev-less', function() {
    return gulp.src(['build/**/*.less','build/**/*.css'])
        .pipe(watch(['build/**/*.less','build/**/*.css']))  
        .pipe(less())
        .pipe(gulp.dest('dist'))
        .pipe(reload({stream: true}))
})

gulp.task('serve', ['clean'], function() {
    gulp.start('dev-es', 'dev-less')
    browserSync.init({
        port: 3003,
        proxy: 'localhost:8001'
    })

    gulp.watch('build/**/*.js', ['dev-es']);
    gulp.watch(['build/**/*.less','build/**/*.css'], ['dev-less']);

})

gulp.task('default', ['serve', 'watch'])


// 监控 build 目录的改动自动编译
// gulp.task('default',['build'],function () {
//     browserSync({
//         open:false,
//         proxy:'localhost:80'
//     });
//      gulp.watch('build/**/*.js',gulpsync.sync(['js-watch']));
//      gulp.watch(['build/**/*.less','build/**/*.css'],gulpsync.sync(['less']));
// });



