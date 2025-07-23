# Python Client Example

This example demonstrates how to create a Python client that interacts with the Spring Boot Drools Integration APIs using the `requests` library.

## 📋 Overview

This Python client provides:
- Simple and clean API integration
- Comprehensive error handling
- Configuration management
- Async support with `aiohttp`
- Unit tests with `pytest`
- Example usage of all major API endpoints

## 🚀 Quick Start

### Prerequisites

- Python 3.8 or higher
- pip package manager
- Spring Boot Drools Integration application running on `http://localhost:8080`

### Installation

```bash
# Navigate to the example directory
cd examples/python

# Create virtual environment
python -m venv venv

# Activate virtual environment
# On Windows:
venv\Scripts\activate
# On macOS/Linux:
source venv/bin/activate

# Install dependencies
pip install -r requirements.txt
```

### Basic Usage

```python
from drools_client import DroolsClient

# Create client instance
client = DroolsClient(
    base_url="http://localhost:8080",
    username="admin",
    password="admin123"
)

# Calculate customer discount
discount_request = {
    "customerId": "CUST001",
    "customerName": "John Doe",
    "age": 35,
    "loyaltyTier": "GOLD",
    "orderAmount": 1000.00,
    "orderItems": 5
}

response = client.calculate_discount(discount_request)
print(f"Discount: {response['discountPercentage']}%")
```

## 📁 Project Structure

```
python/
├── requirements.txt              # Python dependencies
├── requirements-dev.txt          # Development dependencies
├── setup.py                      # Package setup
├── drools_client/
│   ├── __init__.py              # Package initialization
│   ├── client.py                # Main client class
│   ├── models.py                # Data models
│   ├── exceptions.py            # Custom exceptions
│   └── config.py                # Configuration management
├── examples/
│   ├── basic_usage.py           # Basic usage examples
│   ├── batch_processing.py      # Batch processing examples
│   ├── async_client.py          # Async client examples
│   └── error_handling.py        # Error handling examples
├── tests/
│   ├── test_client.py           # Client tests
│   ├── test_models.py           # Model tests
│   └── conftest.py              # Test configuration
└── docs/
    └── api_reference.md         # API reference documentation
```

## ⚙️ Configuration

### Environment Variables

```bash
export DROOLS_API_BASE_URL=http://localhost:8080
export DROOLS_API_USERNAME=admin
export DROOLS_API_PASSWORD=admin123
export DROOLS_API_TIMEOUT=30
```

### Configuration File

Create `config.yaml`:

```yaml
drools:
  api:
    base_url: http://localhost:8080
    username: admin
    password: admin123
    timeout: 30
    retry:
      max_attempts: 3
      delay: 1.0
      backoff_factor: 2.0
```

## 🔧 Key Components

### 1. Main Client Class

```python
import requests
from typing import Dict, List, Optional, Any
from .config import DroolsConfig
from .exceptions import DroolsApiException
from .models import CustomerDiscountRequest, CustomerDiscountResponse

class DroolsClient:
    """Main client for interacting with Drools API."""
    
    def __init__(self, base_url: str = None, username: str = None, 
                 password: str = None, timeout: int = 30):
        self.config = DroolsConfig(base_url, username, password, timeout)
        self.session = requests.Session()
        self.session.auth = (self.config.username, self.config.password)
        self.session.headers.update({
            'Content-Type': 'application/json',
            'Accept': 'application/json'
        })
    
    def calculate_discount(self, request: Dict[str, Any]) -> Dict[str, Any]:
        """Calculate customer discount."""
        return self._post('/api/v1/discounts/calculate', request)
    
    def evaluate_loan(self, request: Dict[str, Any]) -> Dict[str, Any]:
        """Evaluate loan application."""
        return self._post('/api/v1/loan-approval/evaluate', request)
    
    def get_recommendations(self, request: Dict[str, Any]) -> Dict[str, Any]:
        """Get product recommendations."""
        return self._post('/api/v1/product-recommendation/recommend', request)
    
    def execute_rules(self, request: Dict[str, Any]) -> Dict[str, Any]:
        """Execute generic rules."""
        return self._post('/api/v1/rules/execute', request)
    
    def upload_decision_table(self, file_path: str, rule_package: str) -> Dict[str, Any]:
        """Upload decision table file."""
        with open(file_path, 'rb') as file:
            files = {'file': file}
            data = {'rulePackage': rule_package}
            return self._post_multipart('/api/v1/rules/upload', data, files)
    
    def validate_rules(self) -> Dict[str, Any]:
        """Validate all rules."""
        return self._put('/api/v1/rules/validate')
    
    def reload_rules(self) -> Dict[str, Any]:
        """Reload all rules."""
        return self._post('/api/v1/rules/reload')
    
    def get_rule_status(self) -> Dict[str, Any]:
        """Get rule engine status."""
        return self._get('/api/v1/rules/status')
    
    def get_health(self) -> Dict[str, Any]:
        """Get application health."""
        return self._get('/actuator/health')
    
    def _get(self, endpoint: str) -> Dict[str, Any]:
        """Make GET request."""
        return self._request('GET', endpoint)
    
    def _post(self, endpoint: str, data: Dict[str, Any] = None) -> Dict[str, Any]:
        """Make POST request."""
        return self._request('POST', endpoint, json=data)
    
    def _put(self, endpoint: str, data: Dict[str, Any] = None) -> Dict[str, Any]:
        """Make PUT request."""
        return self._request('PUT', endpoint, json=data)
    
    def _post_multipart(self, endpoint: str, data: Dict[str, Any], 
                       files: Dict[str, Any]) -> Dict[str, Any]:
        """Make POST request with multipart data."""
        url = f"{self.config.base_url}{endpoint}"
        
        try:
            response = self.session.post(
                url, data=data, files=files, timeout=self.config.timeout
            )
            response.raise_for_status()
            return response.json()
        except requests.exceptions.RequestException as e:
            raise DroolsApiException(f"API call failed: {endpoint}", e)
    
    def _request(self, method: str, endpoint: str, **kwargs) -> Dict[str, Any]:
        """Make HTTP request."""
        url = f"{self.config.base_url}{endpoint}"
        
        try:
            response = self.session.request(
                method, url, timeout=self.config.timeout, **kwargs
            )
            response.raise_for_status()
            return response.json()
        except requests.exceptions.RequestException as e:
            raise DroolsApiException(f"API call failed: {endpoint}", e)
```

### 2. Data Models

```python
from dataclasses import dataclass
from typing import List, Optional, Any
from decimal import Decimal

@dataclass
class CustomerDiscountRequest:
    """Customer discount request model."""
    customer_id: str
    customer_name: str
    age: int
    loyalty_tier: str
    order_amount: Decimal
    order_items: int
    membership_years: Optional[int] = None
    previous_orders: Optional[int] = None
    
    def to_dict(self) -> dict:
        """Convert to dictionary for API call."""
        return {
            'customerId': self.customer_id,
            'customerName': self.customer_name,
            'age': self.age,
            'loyaltyTier': self.loyalty_tier,
            'orderAmount': float(self.order_amount),
            'orderItems': self.order_items,
            'membershipYears': self.membership_years,
            'previousOrders': self.previous_orders
        }

@dataclass
class CustomerDiscountResponse:
    """Customer discount response model."""
    customer_id: str
    customer_name: str
    original_amount: Decimal
    discount_percentage: float
    discount_amount: Decimal
    final_amount: Decimal
    applied_rules: List[str]
    calculation_timestamp: str
    
    @classmethod
    def from_dict(cls, data: dict) -> 'CustomerDiscountResponse':
        """Create from API response dictionary."""
        return cls(
            customer_id=data['customerId'],
            customer_name=data['customerName'],
            original_amount=Decimal(str(data['originalAmount'])),
            discount_percentage=data['discountPercentage'],
            discount_amount=Decimal(str(data['discountAmount'])),
            final_amount=Decimal(str(data['finalAmount'])),
            applied_rules=data['appliedRules'],
            calculation_timestamp=data['calculationTimestamp']
        )

@dataclass
class LoanApprovalRequest:
    """Loan approval request model."""
    applicant_id: str
    applicant_name: str
    age: int
    credit_score: int
    annual_income: Decimal
    loan_amount: Decimal
    employment_years: int
    existing_debts: Optional[Decimal] = None
    loan_purpose: Optional[str] = None
    down_payment: Optional[Decimal] = None
    
    def to_dict(self) -> dict:
        """Convert to dictionary for API call."""
        return {
            'applicantId': self.applicant_id,
            'applicantName': self.applicant_name,
            'age': self.age,
            'creditScore': self.credit_score,
            'annualIncome': float(self.annual_income),
            'loanAmount': float(self.loan_amount),
            'employmentYears': self.employment_years,
            'existingDebts': float(self.existing_debts) if self.existing_debts else None,
            'loanPurpose': self.loan_purpose,
            'downPayment': float(self.down_payment) if self.down_payment else None
        }
```

### 3. Custom Exceptions

```python
class DroolsApiException(Exception):
    """Custom exception for Drools API errors."""
    
    def __init__(self, message: str, original_exception: Exception = None):
        super().__init__(message)
        self.original_exception = original_exception
        self.status_code = None
        self.response_text = None
        
        if hasattr(original_exception, 'response'):
            response = original_exception.response
            self.status_code = response.status_code
            self.response_text = response.text

class DroolsConfigurationError(Exception):
    """Exception for configuration errors."""
    pass

class DroolsValidationError(Exception):
    """Exception for validation errors."""
    pass
```

### 4. Configuration Management

```python
import os
import yaml
from typing import Optional

class DroolsConfig:
    """Configuration management for Drools client."""
    
    def __init__(self, base_url: str = None, username: str = None, 
                 password: str = None, timeout: int = None):
        # Load from environment variables or parameters
        self.base_url = base_url or os.getenv('DROOLS_API_BASE_URL', 'http://localhost:8080')
        self.username = username or os.getenv('DROOLS_API_USERNAME', 'admin')
        self.password = password or os.getenv('DROOLS_API_PASSWORD', 'admin123')
        self.timeout = timeout or int(os.getenv('DROOLS_API_TIMEOUT', '30'))
        
        # Load from config file if exists
        self._load_from_file()
        
        # Validate configuration
        self._validate()
    
    def _load_from_file(self):
        """Load configuration from YAML file."""
        config_file = os.getenv('DROOLS_CONFIG_FILE', 'config.yaml')
        if os.path.exists(config_file):
            with open(config_file, 'r') as f:
                config = yaml.safe_load(f)
                drools_config = config.get('drools', {}).get('api', {})
                
                self.base_url = drools_config.get('base_url', self.base_url)
                self.username = drools_config.get('username', self.username)
                self.password = drools_config.get('password', self.password)
                self.timeout = drools_config.get('timeout', self.timeout)
    
    def _validate(self):
        """Validate configuration."""
        if not self.base_url:
            raise DroolsConfigurationError("Base URL is required")
        if not self.username:
            raise DroolsConfigurationError("Username is required")
        if not self.password:
            raise DroolsConfigurationError("Password is required")
```

## 🧪 Testing

### Unit Tests

```python
import pytest
from unittest.mock import Mock, patch
from drools_client import DroolsClient
from drools_client.exceptions import DroolsApiException

class TestDroolsClient:
    
    @pytest.fixture
    def client(self):
        return DroolsClient(
            base_url="http://localhost:8080",
            username="admin",
            password="admin123"
        )
    
    @patch('drools_client.client.requests.Session.post')
    def test_calculate_discount_success(self, mock_post, client):
        # Given
        mock_response = Mock()
        mock_response.json.return_value = {
            'customerId': 'CUST001',
            'discountPercentage': 15.0,
            'discountAmount': 150.0
        }
        mock_response.raise_for_status.return_value = None
        mock_post.return_value = mock_response
        
        request = {
            'customerId': 'CUST001',
            'age': 35,
            'loyaltyTier': 'GOLD',
            'orderAmount': 1000.0
        }
        
        # When
        response = client.calculate_discount(request)
        
        # Then
        assert response['customerId'] == 'CUST001'
        assert response['discountPercentage'] == 15.0
        mock_post.assert_called_once()
    
    @patch('drools_client.client.requests.Session.post')
    def test_calculate_discount_api_error(self, mock_post, client):
        # Given
        mock_post.side_effect = requests.exceptions.HTTPError("API Error")
        
        request = {'customerId': 'CUST001'}
        
        # When/Then
        with pytest.raises(DroolsApiException):
            client.calculate_discount(request)
```

### Integration Tests

```python
import pytest
from drools_client import DroolsClient
from decimal import Decimal

@pytest.mark.integration
class TestDroolsClientIntegration:
    
    @pytest.fixture
    def client(self):
        return DroolsClient()  # Uses default configuration
    
    def test_health_check(self, client):
        """Test application health check."""
        response = client.get_health()
        assert response['status'] == 'UP'
    
    def test_calculate_discount_integration(self, client):
        """Test discount calculation integration."""
        request = {
            'customerId': 'CUST001',
            'customerName': 'John Doe',
            'age': 35,
            'loyaltyTier': 'GOLD',
            'orderAmount': 1000.0,
            'orderItems': 5
        }
        
        response = client.calculate_discount(request)
        
        assert response['customerId'] == 'CUST001'
        assert 'discountPercentage' in response
        assert response['discountPercentage'] >= 0
```

## 📊 Usage Examples

### Example 1: Basic Discount Calculation

```python
#!/usr/bin/env python3
"""Basic discount calculation example."""

from drools_client import DroolsClient
from drools_client.models import CustomerDiscountRequest
from decimal import Decimal

def main():
    # Create client
    client = DroolsClient()
    
    # Create request
    request = CustomerDiscountRequest(
        customer_id="CUST001",
        customer_name="John Doe",
        age=35,
        loyalty_tier="GOLD",
        order_amount=Decimal("1000.00"),
        order_items=5,
        membership_years=3
    )
    
    try:
        # Calculate discount
        response = client.calculate_discount(request.to_dict())
        
        print(f"Customer: {response['customerName']}")
        print(f"Original Amount: ${response['originalAmount']}")
        print(f"Discount: {response['discountPercentage']}%")
        print(f"Final Amount: ${response['finalAmount']}")
        print(f"Applied Rules: {', '.join(response['appliedRules'])}")
        
    except Exception as e:
        print(f"Error: {e}")

if __name__ == "__main__":
    main()
```

### Example 2: Batch Processing

```python
#!/usr/bin/env python3
"""Batch processing example."""

from drools_client import DroolsClient
from concurrent.futures import ThreadPoolExecutor, as_completed
import time

def calculate_discount_for_customer(client, customer_data):
    """Calculate discount for a single customer."""
    try:
        response = client.calculate_discount(customer_data)
        return {
            'success': True,
            'customer_id': customer_data['customerId'],
            'discount': response['discountPercentage']
        }
    except Exception as e:
        return {
            'success': False,
            'customer_id': customer_data['customerId'],
            'error': str(e)
        }

def main():
    client = DroolsClient()
    
    # Sample customer data
    customers = [
        {
            'customerId': f'CUST{i:03d}',
            'customerName': f'Customer {i}',
            'age': 25 + (i % 40),
            'loyaltyTier': ['BRONZE', 'SILVER', 'GOLD'][i % 3],
            'orderAmount': 100.0 + (i * 50),
            'orderItems': 1 + (i % 10)
        }
        for i in range(1, 21)  # 20 customers
    ]
    
    print(f"Processing {len(customers)} customers...")
    start_time = time.time()
    
    # Process in parallel
    with ThreadPoolExecutor(max_workers=5) as executor:
        futures = [
            executor.submit(calculate_discount_for_customer, client, customer)
            for customer in customers
        ]
        
        results = []
        for future in as_completed(futures):
            result = future.result()
            results.append(result)
            
            if result['success']:
                print(f"✓ {result['customer_id']}: {result['discount']}% discount")
            else:
                print(f"✗ {result['customer_id']}: {result['error']}")
    
    end_time = time.time()
    successful = sum(1 for r in results if r['success'])
    
    print(f"\nProcessed {len(customers)} customers in {end_time - start_time:.2f}s")
    print(f"Success rate: {successful}/{len(customers)} ({successful/len(customers)*100:.1f}%)")

if __name__ == "__main__":
    main()
```

### Example 3: Async Client

```python
#!/usr/bin/env python3
"""Async client example using aiohttp."""

import asyncio
import aiohttp
from typing import Dict, Any

class AsyncDroolsClient:
    """Async version of Drools client."""
    
    def __init__(self, base_url: str = "http://localhost:8080",
                 username: str = "admin", password: str = "admin123"):
        self.base_url = base_url
        self.auth = aiohttp.BasicAuth(username, password)
        self.headers = {
            'Content-Type': 'application/json',
            'Accept': 'application/json'
        }
    
    async def calculate_discount(self, request: Dict[str, Any]) -> Dict[str, Any]:
        """Calculate customer discount asynchronously."""
        async with aiohttp.ClientSession(auth=self.auth, headers=self.headers) as session:
            url = f"{self.base_url}/api/v1/discounts/calculate"
            async with session.post(url, json=request) as response:
                response.raise_for_status()
                return await response.json()
    
    async def evaluate_loan(self, request: Dict[str, Any]) -> Dict[str, Any]:
        """Evaluate loan application asynchronously."""
        async with aiohttp.ClientSession(auth=self.auth, headers=self.headers) as session:
            url = f"{self.base_url}/api/v1/loan-approval/evaluate"
            async with session.post(url, json=request) as response:
                response.raise_for_status()
                return await response.json()

async def main():
    client = AsyncDroolsClient()
    
    # Prepare multiple requests
    discount_requests = [
        {
            'customerId': f'CUST{i:03d}',
            'customerName': f'Customer {i}',
            'age': 25 + (i % 40),
            'loyaltyTier': ['BRONZE', 'SILVER', 'GOLD'][i % 3],
            'orderAmount': 100.0 + (i * 50),
            'orderItems': 1 + (i % 10)
        }
        for i in range(1, 11)
    ]
    
    # Process all requests concurrently
    tasks = [client.calculate_discount(request) for request in discount_requests]
    responses = await asyncio.gather(*tasks, return_exceptions=True)
    
    # Process results
    for i, response in enumerate(responses):
        if isinstance(response, Exception):
            print(f"Error for customer {i+1}: {response}")
        else:
            print(f"Customer {response['customerId']}: {response['discountPercentage']}% discount")

if __name__ == "__main__":
    asyncio.run(main())
```

## 🔍 Error Handling

### Comprehensive Error Handling Example

```python
#!/usr/bin/env python3
"""Error handling example."""

from drools_client import DroolsClient
from drools_client.exceptions import DroolsApiException
import requests
import logging

# Configure logging
logging.basicConfig(level=logging.INFO)
logger = logging.getLogger(__name__)

def handle_api_errors(client: DroolsClient, request: dict):
    """Demonstrate comprehensive error handling."""
    
    try:
        response = client.calculate_discount(request)
        logger.info(f"Success: {response['customerId']} - {response['discountPercentage']}%")
        return response
        
    except DroolsApiException as e:
        if e.status_code == 401:
            logger.error("Authentication failed - check credentials")
        elif e.status_code == 400:
            logger.error(f"Bad request - invalid data: {e.response_text}")
        elif e.status_code == 500:
            logger.error("Server error - check application logs")
        else:
            logger.error(f"API error ({e.status_code}): {e}")
        return None
        
    except requests.exceptions.ConnectionError:
        logger.error("Connection failed - check if service is running")
        return None
        
    except requests.exceptions.Timeout:
        logger.error("Request timeout - service may be overloaded")
        return None
        
    except Exception as e:
        logger.error(f"Unexpected error: {e}")
        return None

def main():
    client = DroolsClient()
    
    # Test various scenarios
    test_cases = [
        # Valid request
        {
            'customerId': 'CUST001',
            'customerName': 'John Doe',
            'age': 35,
            'loyaltyTier': 'GOLD',
            'orderAmount': 1000.0,
            'orderItems': 5
        },
        # Invalid request (missing required field)
        {
            'customerId': 'CUST002',
            'customerName': 'Jane Doe',
            # Missing age field
            'loyaltyTier': 'SILVER',
            'orderAmount': 500.0,
            'orderItems': 3
        },
        # Invalid data types
        {
            'customerId': 'CUST003',
            'customerName': 'Bob Smith',
            'age': 'thirty-five',  # Should be integer
            'loyaltyTier': 'BRONZE',
            'orderAmount': 'one thousand',  # Should be number
            'orderItems': 2
        }
    ]
    
    for i, test_case in enumerate(test_cases, 1):
        logger.info(f"Testing case {i}: {test_case['customerId']}")
        result = handle_api_errors(client, test_case)
        if result:
            logger.info(f"Case {i} succeeded")
        else:
            logger.info(f"Case {i} failed")
        print("-" * 50)

if __name__ == "__main__":
    main()
```

## 📦 Dependencies

### requirements.txt

```
requests>=2.28.0
pyyaml>=6.0
aiohttp>=3.8.0
python-dotenv>=0.19.0
```

### requirements-dev.txt

```
pytest>=7.0.0
pytest-asyncio>=0.21.0
pytest-mock>=3.10.0
pytest-cov>=4.0.0
black>=22.0.0
flake8>=5.0.0
mypy>=1.0.0
```

## 🚀 Advanced Features

### Retry Logic with Exponential Backoff

```python
import time
import random
from functools import wraps

def retry_with_backoff(max_attempts=3, base_delay=1.0, max_delay=60.0, backoff_factor=2.0):
    """Decorator for retry logic with exponential backoff."""
    
    def decorator(func):
        @wraps(func)
        def wrapper(*args, **kwargs):
            last_exception = None
            
            for attempt in range(max_attempts):
                try:
                    return func(*args, **kwargs)
                except (requests.exceptions.RequestException, DroolsApiException) as e:
                    last_exception = e
                    
                    if attempt == max_attempts - 1:
                        break
                    
                    # Calculate delay with jitter
                    delay = min(base_delay * (backoff_factor ** attempt), max_delay)
                    jitter = random.uniform(0, 0.1) * delay
                    time.sleep(delay + jitter)
                    
                    logger.warning(f"Attempt {attempt + 1} failed, retrying in {delay:.2f}s: {e}")
            
            raise last_exception
        
        return wrapper
    return decorator

class ResilientDroolsClient(DroolsClient):
    """Drools client with built-in retry logic."""
    
    @retry_with_backoff(max_attempts=3)
    def calculate_discount(self, request: Dict[str, Any]) -> Dict[str, Any]:
        return super().calculate_discount(request)
    
    @retry_with_backoff(max_attempts=3)
    def evaluate_loan(self, request: Dict[str, Any]) -> Dict[str, Any]:
        return super().evaluate_loan(request)
```

## 📚 Additional Resources

- [Requests Documentation](https://docs.python-requests.org/)
- [aiohttp Documentation](https://docs.aiohttp.org/)
- [pytest Documentation](https://docs.pytest.org/)
- [Python Type Hints](https://docs.python.org/3/library/typing.html)

## 🤝 Contributing

To extend this example:

1. Add new API endpoint methods
2. Implement additional error handling scenarios
3. Add more comprehensive tests
4. Include performance testing examples
5. Add support for additional authentication methods

---

**This Python client provides a robust foundation for integrating with the Drools API in Python applications.**