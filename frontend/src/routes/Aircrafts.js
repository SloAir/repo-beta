import React from 'react';
import axios from 'axios';
import Table from '../components/Table';

class Aircrafts extends React.Component {

    state = { 
      details: [],
      modalOpen: false,
      editRow: null,
    }

    componentDidMount() {
        let data;
        axios.get('http://localhost:8000/api/aircraft/get/')
        .then(res => {
            data = res.data;
            this.setState({
              details: data
            });
        })
        .catch(err => { })
    }

    handleEdit = id => {
      this.setState({ editRow: id, modalOpen: true, });
    }

    handleCloseModal = () => {
      this.setState({ modalOpen: false, editRow: null });
    }
    
    handleDelete = id => {
      const deletedIndex = this.state.details.findIndex(
        obj => obj._id === id
      );

      const deletedObject = this.state.details[deletedIndex];

      axios.delete(`http://localhost:8000/api/aircraft/delete/${deletedObject.registration}/`)
      .then((response) => {
          console.log(response);

          const updatedData = [...this.state.details];
          updatedData.splice(deletedIndex, 1);
          this.setState({ details: updatedData });
      })
      .catch((error) => {
        console.error(error);
      });
    }

    handleSubmit = formData => {
      const editedIndex = this.state.details.findIndex(
        obj => obj._id == formData._id
      );
      
      axios.put('http://localhost:8000/api/aircraft/put/', formData)
      .then((response) => {
          console.log(response);

          const updatedData = [...this.state.details];
          updatedData[editedIndex] = formData;
          this.setState({ details: updatedData });
      })
      .catch((error) => {
        console.error(error);
      });
    }

    render() {
        const formDataStructure = {
          registration: '',
          model: {
            code: '',
            text: '',
          },
        };

        return (
          <div className='App'>
            <Table 
              data={this.state.details} 
              formDataStruct={formDataStructure}
              onEdit={this.handleEdit}
              onSubmit={this.handleSubmit}
              onDelete={this.handleDelete}
            />
          </div>
        )
    }
}

export default Aircrafts;