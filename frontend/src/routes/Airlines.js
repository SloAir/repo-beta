import React from 'react';
import axios from 'axios';
import Table from '../components/Table';

class Airlines extends React.Component {

  state = { 
    details: [],
    modalOpen: false,
    editRow: null,
  }

    componentDidMount() {
        let data;
        axios.get('http://localhost:8000/api/airline/get/')
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

    handleSubmit = formData => {
      // Handle the form submission here (e.g., axios.put request)
      console.log('Submitting form:', formData);
      // Update the state or make API call to update the data in the table
      // ...
  
      // Close the modal
      this.handleCloseModal();
    }

    render() {
      const formDataStructure = {
        name: '',
        short: '',
        code: {
          iata: '',
          icao: '',
        },
        url: '',
      };

      return (
        <div className='App'>
          <Table 
            data={this.state.details} 
            formDataStruct={formDataStructure}
            onEdit={this.handleEdit}
            onSubmit={this.handleSubmit}
          />
        </div>
      )
  }
}

export default Airlines;
