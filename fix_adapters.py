import os

dir_path = 'app/src/main/java/com/rushi/healthcare_app/adapters/'

for file_name in os.listdir(dir_path):
    if file_name.endswith('.java'):
        path = os.path.join(dir_path, file_name)
        with open(path, 'r', encoding='utf-8') as f:
            content = f.read()

        new_content = content.replace('MaterialButton btnEdit', 'android.widget.ImageView btnEdit')
        new_content = new_content.replace('MaterialButton btnDelete', 'android.widget.ImageView btnDelete')
        
        if new_content != content:
            with open(path, 'w', encoding='utf-8') as f:
                f.write(new_content)
            print(f'Fixed {file_name}')

