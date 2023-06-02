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
      axios.get('http://localhost:8000/api/airport/get/')
        .then(res => {
          const data = res.data;
          const cleanedData = data.map(entry => {
            if (entry.website) {
              const parts = entry.website.split('/'); // Split the URL into parts using forward slash as the separator
              const modifiedWebsite = parts.slice(0, 3).join('/'); // Join the first three parts back together with forward slashes
              return {
                ...entry,
                website: modifiedWebsite
              };
            } else {
              return entry; // Skip entries without a website
            }
          });
          this.setState({
            details: cleanedData
          });
        })
        .catch(err => {

        });
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
          code: {
            iata: '',
            icao: '',
          },
          position: {
            country: {
              name: '',
            },
          },
          timezone: {
            abbr: '',
          },
          website: '',
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
