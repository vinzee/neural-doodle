import os
from flask import Flask, request, redirect, url_for
from flask import send_from_directory
from flask import flash
from flask import jsonify
import subprocess
import time
from shutil import copyfile
import random

UPLOAD_FOLDER = '/media/vipin/SSD/Grad_study/Spring 2018/Mobile Computing/FinalProject/neural-doodle/resources/uploads'
RESULT_FOLDER = '/media/vipin/SSD/Grad_study/Spring 2018/Mobile Computing/FinalProject/neural-doodle/webapp'
ALLOWED_EXTENSIONS = set(['png', 'jpg', 'jpeg'])

style_dict = {
    'renoir': '../../../project/fast-neural-doodle/data/Renoir/style.png',
    'monet' : '../../../project/fast-neural-doodle/data/Monet/style.png',
    'gogh': '../../../project/fast-neural-doodle/data/Van_Gogh/portrait.jpg'
}
style_mask_dict = {
    'renoir': '../../../project/fast-neural-doodle/data/Renoir/style_mask.png',
    'monet' : '../../../project/fast-neural-doodle/data/Monet/style_mask.png',
    'gogh': '../../../project/fast-neural-doodle/data/Van_Gogh/portrait_mask.png'
}

app = Flask(__name__)
app.config['UPLOAD_FOLDER'] = UPLOAD_FOLDER
app.config['RESULT_FOLDER'] = RESULT_FOLDER
app.add_url_rule('/uploads/<filename>', 'uploaded_file', build_only=True)

@app.route('/index')
def index():
    return 'Hello world'

def allowed_file(filename):
    return '.' in filename and \
           filename.rsplit('.', 1)[1].lower() in ALLOWED_EXTENSIONS

@app.route('/', methods=['GET', 'POST'])
def upload_file():
    # print str(request)
    # print str(request.args)
    # return
    # print str(request.form)
    # print str(request.files)
    if request.method == 'POST':
        # check if the post request has the file part
        if 'file' not in request.files:
            flash('No file part')
            return redirect(request.url)
        file = request.files['file']
        # if user does not select file, browser also
        # submit a empty part without filename
        #if file.filename == '':
        #    flash('No selected file')
        #    return redirect(request.url)i
	# filename = 'test.jpg'
        if file and allowed_file(file.filename):
            style = request.args['style']

            filename = str(random.randint(1, 100)) + '.png'
            target_mask_path = os.path.join(app.config['UPLOAD_FOLDER'], filename)
            # file.filepath = target_mask_path
            file.save(target_mask_path)
            
            copyfile(target_mask_path, os.path.join('static/results', filename))

            print 'Saving file to : ' + str(target_mask_path) 
            # p1 = subprocess.Popen(["python ../../../project/fast-neural-doodle/get_mask_hdf5.py --n_colors=4 --style_image=../../../project/fast-neural-doodle/data/Renoir/style.png --style_mask=../../../project/fast-neural-doodle/data/Renoir/style_mask.png --target_mask=../../../project/fast-neural-doodle/data/Renoir/target_mask.png"], shell=True)
            process1_cmd = "python ../../../project/fast-neural-doodle/get_mask_hdf5.py --n_colors=4 --style_image=" + style_dict[style] + " --style_mask=" + style_mask_dict[style] + " --target_mask=../resources/uploads/" + str(filename)
            process2_cmd = "th ../../../project/fast-neural-doodle/fast_neural_doodle.lua -masks_hdf5 masks.hdf5 -output_image static/results/" + filename
            p1 = subprocess.Popen([process1_cmd + " && "  + process2_cmd], shell=True)
            # p2 = subprocess.Popen([], shell=True)
            # p1.wait()
            # p2.wait()
            # if not os.path.exists('/media/vipin/SSD/Grad_study/Spring 2018/Mobile Computing/FinalProject/neural-doodle/webapp/static/results/out.png'):
            #     print 'Waiting for results'
            #     time.sleep(10)
            
            # return redirect(url_for('uploaded_file', filename=filename))
            
            print 'Sending result url:  ' + 'static/results/' + filename
            return 'static/results/' + filename
    return '''
    <!doctype html>
    <title>Upload new File</title>
    <h1>Upload new File</h1>
    <form method=post enctype=multipart/form-data>
      <p><input type=file name=file>
         <input type=submit value=Upload>
    </form>
    '''


@app.route('/uploads/<filename>')
def uploaded_file(filename):
    print 'Result folder: ' + str(app.config['RESULT_FOLDER'])
    return send_from_directory(app.config['RESULT_FOLDER'], filename)


@app.route('/doodle')
def doodle():
    return 'Checking doodle API!!'

if __name__ == '__main__':
#    app.run(debug=True, host='0.0.0.0')
    # app.run(debug=True, host='130.85.94.233')
    app.run(debug=True, host='localhost')
