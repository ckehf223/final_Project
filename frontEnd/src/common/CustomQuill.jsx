import React, { useRef } from 'react';
import ReactQuill from 'react-quill';
import 'react-quill/dist/quill.snow.css';
import { CustomToolbar } from '/src/common/CustomToolbar';

const CustomQuill = ({ content, setContent, width, height }) => {
  const quillRef = useRef(null);
  const handleEditorChange = (value) => {
    setContent(value);
  };

  const modules = {
    toolbar: {
      container: "#toolbar",
    },
  };

  const formats = [
    'header', 'font', 'size',
    'bold', 'italic', 'underline', 'strike',
    'blockquote', 'list', 'bullet', 'indent',
    'link', 'image', 'video', 'color', 'background'
  ];

  return (
    <div className='CustomQuill'>
      <CustomToolbar width={width} />
      <ReactQuill
        ref={quillRef}
        value={content}
        onChange={handleEditorChange}
        modules={modules}
        formats={formats}
        style={{ width: `${width}px`, height: `${height}px`, }}
      />
    </div>
  );
}

export default CustomQuill;
