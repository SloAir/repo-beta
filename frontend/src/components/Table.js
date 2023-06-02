import React from 'react'
import { BsFillTrashFill, BsFillPencilFill } from "react-icons/bs";
import './Table.css';
import Modal from './Modal';

class Table extends React.Component {

    state = {
        editRow: null,
        formData: this.props.formDataStruct,
        propertyCalls: []
    }

    handleEdit = id => {
        this.setState({ editRow: id });
        console.log(`Editing object with ID: ${id} ...`)
    }

    handleDelete = id => {
        console.log(this.generatePropertyCalls(this.state.formData));
        console.log(`Deleting object with ID: ${id} ...`)
    }

    generateTableHeader = (fieldName, index) => {
        const capitalizedFieldName = fieldName.charAt(0).toUpperCase() + fieldName.slice(1);

        return (
            <th key={index} className='expand'>{capitalizedFieldName}</th>
        );
    }
    
    generatePropertyCalls = (formDataStructure, parentKey = '') => {
        const propertyCalls = [];
      
        for (const key in formDataStructure) {
          const currentKey = parentKey ? `${parentKey}.${key}` : key;
      
          if (typeof formDataStructure[key] === 'object' && formDataStructure[key] !== null) {
            const nestedCalls = this.generatePropertyCalls(formDataStructure[key], currentKey);
            propertyCalls.push(...nestedCalls);
          } else {
            const propertyCall = `formData.${currentKey}`;
            propertyCalls.push(propertyCall);
          }
        }
      
        return propertyCalls;
    };
      
    render() {
        const { data } = this.props;
        const { editRow } = this.state;
        const { onSubmit } = this.props;
        const { formData } = this.state;

        const tableHeaders = Object.entries(formData).flatMap(([fieldName, fieldValue], index) => {
            if (typeof fieldValue === 'object') {
                return Object.entries(fieldValue).map(([nestedFieldName, nestedFieldValue]) => {
                    return this.generateTableHeader(`${fieldName}.${nestedFieldName}`, index);
                });
            }
            
            return this.generateTableHeader(fieldName, index);
        });

        const propertyCalls = this.generatePropertyCalls(formData);

        const trimmedPropertyCalls = propertyCalls.map((propertyCall) => {
            const dotIndex = propertyCall.indexOf('.');
            return propertyCall.substring(dotIndex + 1);
        });

        const tableData = data.map((object) => (
            <tr key={object._id}>
              {trimmedPropertyCalls.map((trimmedPropertyCall, index) => {
                let propertyValue = null;
                try {
                  propertyValue = eval(`object.${trimmedPropertyCall}`);
                } catch (error) {
                  //console.error(`Error evaluating property: ${trimmedPropertyCall}`, error);
                }
                return <td key={index}>{propertyValue}</td>;
              })}
              <td>
                <span className='actions'>
                  <BsFillPencilFill className='edit-btn' onClick={() => this.handleEdit(object._id)} />
                  <BsFillTrashFill className='delete-btn' onClick={() => this.handleDelete(object._id)} />
                </span>
              </td>
            </tr>
        ));
          

        return (
            <div className='table-wrapper'>
                <table className='table'>
                    <thead>
                        <tr>
                            {tableHeaders}
                            <th>Action</th>
                        </tr>
                    </thead>
                    <tbody>
                        {tableData}
                    </tbody>
                </table>
                {editRow && (
                    <Modal
                        object={data.find((obj) => obj._id === editRow)}
                        closeModal={() => this.setState({ editRow: null })}
                        onSubmit={onSubmit}
                        formDataStruct={formData}
                    />
                )}
            </div>
        )
    }
}

export default Table;