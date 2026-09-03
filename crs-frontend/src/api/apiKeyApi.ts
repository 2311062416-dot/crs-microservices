import axios from 'axios';

const getAuthHeader = () => {
  const token = localStorage.getItem('crs_token');

  // IN RA CONSOLE ĐỂ KIỂM TRA
  console.log("Token trong localStorage:", token);

  return {
    headers: {
      Authorization: `Bearer ${token}`
    }
  };
};

export const getApiKeys = () => {
  return axios.get('http://localhost:8080/api/api-keys', getAuthHeader());
};

export const createApiKey = (data: any) => {
  return axios.post('http://localhost:8080/api/api-keys', data, getAuthHeader());
};

export const revokeApiKey = (id: string | number) => {
  return axios.put(`http://localhost:8080/api/api-keys/${id}/revoke`, {}, getAuthHeader());
};