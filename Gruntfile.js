"use strict";

module.exports = function(grunt) {
	grunt
			.initConfig({
				watch : {
					sass : {
						files : "src/main/sass/**/*.scss",
						tasks : [ "sass" ]
					},
					concat : {
						files : "src/main/js/**/*.js",
						tasks : [ "concat", "uglify" ]
					},
					configFiles : {
						files : [ "Gruntfile.js" ],
						options : {
							reload : true
						}
					}
				},
				concat : {
					dev : {
						options : {
							stripBanners : true,
							sourceMap : true,
							process : function(src, filepath) {
								return '// Source: ' + filepath + '\n' + src;
							},
						},
						files : {
							"src/main/web/js/app.js" : "src/main/js/app/*.js",
							"src/main/web/js/app.legacy.js" : "src/main/js/legacy/*.js"
						}
					}
				},
				uglify : {
					options : {
						sourceMap : true,
						compress : {
							drop_console : true
						}
					},
					app : {
						files : {
							"src/main/web/js/app.min.js" : "src/main/web/js/app.js",
							"src/main/web/js/app.legacy.min.js" : "src/main/web/js/app.legacy.js"
						}
					}
				},
				sass : {
					dev : {
						options : {
							style : "expanded",
							unixNewlines : false,
							sourcemap : "inline"
						},
						files : {
							"src/main/web/css/default.css" : "src/main/sass/style-default.scss",
							"src/main/web/css/welcome.css" : "src/main/sass/style-welcome.scss",
							"src/main/web/css/login.css" : "src/main/sass/style-login.scss"
						}
					},
					dist : {
						options : {
							style : "compressed"
						},
						files : {
							"src/main/web/css/default.min.css" : "src/main/sass/style-default.scss",
							"src/main/web/css/welcome.min.css" : "src/main/sass/style-welcome.scss",
							"src/main/web/css/login.min.css" : "src/main/sass/style-login.scss"
						}
					},
				},
				browserSync : {
					default_options : {
						bsFiles : {
							src : [ "src/main/web/**/*.jsp",
									"src/main/web/js/*.js",
									"src/main/web/css/*.css",
									"src/main/web/**/*.html" ]
						},
						options : {
							watchTask : true,
							proxy : "http://localhost:8080/feedreader/",
							reloadDelay : 2000
						}
					}
				}
			});

	grunt.loadNpmTasks("grunt-contrib-sass");
	grunt.loadNpmTasks("grunt-contrib-watch");
	grunt.loadNpmTasks("grunt-contrib-concat");
	grunt.loadNpmTasks('grunt-contrib-uglify');
	grunt.loadNpmTasks("grunt-browser-sync");

	grunt.registerTask("default", [ "browserSync", "watch" ]);
	grunt.registerTask("dist", [ "sass:dist", "concat:dev", "uglify" ]);
	grunt.registerTask("dev", [ "sass:dev", "concat:dev", "uglify" ]);

}
