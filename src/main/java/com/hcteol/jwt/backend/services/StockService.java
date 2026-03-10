// ...existing code...
package com.hcteol.jwt.backend.services;

import com.hcteol.jwt.backend.entities.Stock;
import com.hcteol.jwt.backend.repositories.StockRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class StockService {
        public Stock getStockByStockCode(String stockCode) {
            return stockRepository.findByStockCode(stockCode);
        }
    @Autowired
    private StockRepository stockRepository;

    public List<Stock> getAllStocks() {
        return stockRepository.findAll();
    }

    public Optional<Stock> getStockById(Long id) {
        return stockRepository.findById(id);
    }

    public Stock createStock(Stock stock) {
        return stockRepository.save(stock);
    }

    public Stock updateStock(Long id, Stock stockDetails) {
        return stockRepository.findById(id).map(stock -> {
            stock.setProductId(stockDetails.getProductId());
            stock.setStockCode(stockDetails.getStockCode());
            stock.setCreateDate(stockDetails.getCreateDate());
            return stockRepository.save(stock);
        }).orElseThrow(() -> new RuntimeException("Stock not found with id " + id));
    }

    public void deleteStock(Long id) {
        stockRepository.deleteById(id);
    }
}
