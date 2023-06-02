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
            />
          </div>
        )
    }
}

export default Aircrafts;


/* 

<button className='btn' onClick={() => this.setState({modalOpen: true})}>Add</button>
    

state = { details: [], }

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

    render() {
        return (
          <div>
            <h1>Data from the DB</h1>
            <hr></hr>
            {this.state.details.map((output, id) => (
              <div key={id}>
                <div>
                  <h3>{output.registration}</h3>
                </div>
              </div> 
            ))}
          </div>
        )
    }

<h1>Data from the DB</h1>
<hr></hr>
{this.state.details.map((output, id) => (
  <div key={id}>
    <div>
      <h3>{output.registration}</h3>
    </div>
  </div> 
))}
*/