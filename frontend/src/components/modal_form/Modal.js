import React, { Component } from 'react';
import axios from 'axios';
import './Modal.css';

export default class Modal extends Component {

    state = {
        formData: this.props.formDataStruct,
    }

    closeContainerClick = (e) => {
        if (e.target.className === 'modal-container') {
            this.props.closeModal();
            this.setState({ formData: {}, });
        }
    }

    handleChange = (e) => {
        const { name, value } = e.target;
        const updatedFormData = this.updateNestedProperty(this.state.formData, name, value);
        this.setState({ formData: updatedFormData });
    };
      
    updateNestedProperty = (formData, name, value) => {
        const [currentKey, ...nestedKeys] = name.split('.');
        
        if (nestedKeys.length === 0) {
            return { ...formData, [currentKey]: value };
        }
        
        return {
            ...formData,
            [currentKey]: this.updateNestedProperty(formData[currentKey], nestedKeys.join('.'), value),
        };
    };

    handleSubmit = (e) => {
        e.preventDefault();
        const { onSubmit, object } = this.props;
        const { formData } = this.state;

        const updatedObject = { ...object };

        const mergeProperties = (target, source) => {
            for (const key in source) {
              if (source.hasOwnProperty(key)) {
                const sourceValue = source[key];
          
                if (sourceValue !== null && sourceValue !== '') {
                  if (typeof sourceValue === 'object' && !Array.isArray(sourceValue)) {
                    target[key] = mergeProperties(target[key] || {}, sourceValue);
                  } else {
                    target[key] = sourceValue;
                  }
                } else if (target.hasOwnProperty(key)) {
                  target[key] = target[key];
                }
              }
            }
          
            return target;
          };
          
        mergeProperties(updatedObject, formData);
        
        console.log(updatedObject);

        onSubmit(updatedObject);
        this.props.closeModal();
    };

    generateFormField = (fieldName, fieldValue, index1, index2, index3) => {
        const capitalizedFieldName = fieldName.charAt(0).toUpperCase() + fieldName.slice(1);
        const key = `${fieldName}-${index1}-${index2}-${index3}`;
      
        return (
          <div className="form-group" key={key}>
            <label htmlFor={key}>{capitalizedFieldName}</label>
            <input
              type="text"
              name={fieldName}
              value={fieldValue}
              onChange={this.handleChange}
            />
          </div>
        );
      };
      
      

    render() {
        const { object } = this.props;
        const { formData } = this.state; 

        const formFields = Object.entries(formData).flatMap(([fieldName, fieldValue], index1) => {
            if (typeof fieldValue === 'object') {
              // Generate nested form fields
              return Object.entries(fieldValue).flatMap(([nestedFieldName, nestedFieldValue], index2) => {
                if (typeof nestedFieldValue === 'object') {
                  // Generate triple nested form fields
                  return Object.entries(nestedFieldValue).map(([tripleNestedFieldName, tripleNestedFieldValue], index3) => {
                    const value = tripleNestedFieldValue || object[fieldName]?.[nestedFieldName]?.[tripleNestedFieldName] || '';
                    return this.generateFormField(`${fieldName}.${nestedFieldName}.${tripleNestedFieldName}`, value, index1, index2, index3);
                  });
                }
          
                const value = nestedFieldValue || object[fieldName]?.[nestedFieldName] || '';
                return this.generateFormField(`${fieldName}.${nestedFieldName}`, value, index1, index2);
              });
            }
          
            // Generate top-level form fields
            const value = fieldValue || object[fieldName] || '';
            return this.generateFormField(fieldName, value, index1);
        });
          

        return (
            <div className='modal-container' onClick={this.closeContainerClick}>
                <div className='modal'>
                    <form>
                        {formFields}
                        <button type='submit' className='btn' onClick={this.handleSubmit}>Edit</button>
                    </form>
                </div>
            </div>
        )
    }
}

/*
                        <div className='form-group'>
                            <label htmlFor='registration'>Registration Number</label>
                            <input 
                                type='text'
                                name='registration'
                                value={formData.registration || object.registration || ''}
                                onChange={this.handleChange}
                            />
                        </div>
                        <div className='form-group'>
                            <label htmlFor='code'>Model Code</label>
                            <input
                                type='text'
                                name='code'
                                value={formData.model.code || object.model.code || ''}
                                onChange={this.handleChange}
                            />
                        </div>
                        <div className='form-group'>
                            <label htmlFor='text'>Model Text</label>
                            <input
                                type='text'
                                name='text'
                                value={formData.model.text || object.model.text || ''}
                                onChange={this.handleChange}
                            />
                        </div>
*/
